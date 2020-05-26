package eutros.metabotany.common.config;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import eutros.metabotany.api.internal.config.Configurable;
import eutros.metabotany.api.internal.config.ITomlSerializer;
import eutros.metabotany.common.utils.Reference;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.moddiscovery.ModAnnotation;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

// TODO make this a separate mod

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MetaBotanyConfig {

    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final Client CLIENT;
    public static final ForgeConfigSpec SERVER_SPEC;
    public static final Server SERVER;
    private static final Logger LOGGER = LogManager.getLogger();
    private static Map<Type, ITomlSerializer<?, ?>> serializers = new HashMap<>();

    static {
        org.objectweb.asm.Type annotationType = org.objectweb.asm.Type.getType(Configurable.class);
        List<ModFileScanData> allScanData = ModList.get().getAllScanData();

        for(ModFileScanData scanData : allScanData) {
            Iterable<ModFileScanData.AnnotationData> annotations = scanData.getAnnotations();
            for(ModFileScanData.AnnotationData a : annotations) {
                if(Objects.equals(a.getAnnotationType(), annotationType)) {
                    Object side = a.getAnnotationData().get("side");
                    switch(side instanceof ModAnnotation.EnumHolder ?
                           ((ModAnnotation.EnumHolder) side).getValue() :
                           "COMMON") {
                        case "COMMON":
                            Common.data.add(a);
                            break;
                        case "CLIENT":
                            Client.data.add(a);
                            break;
                        case "SERVER":
                            Server.data.add(a);
                    }
                }
            }
        }
    }

    static { // TODO move these

        serializers.put(Set.class, new ITomlSerializer<Set<?>, List<?>>() {
            @Override
            public List<?> serialize(Set<?> toSerialize) {
                return toSerialize.stream().map(i -> MetaBotanyConfig.serialize(i, i.getClass())).collect(Collectors.toList());
            }

            @Override
            public Set<?> deserialize(List<?> toDeserialize, Type type) {
                return toDeserialize.stream().map(i -> MetaBotanyConfig.deserialize(i, ((ParameterizedType) type).getActualTypeArguments()[0]))
                        .collect(Collectors.toSet());
            }
        });
        serializers.put(Map.class, new ITomlSerializer<Map<?, ?>, List<List<?>>>() {
            @Override
            public List<List<?>> serialize(Map<?, ?> toSerialize) {
                return toSerialize.keySet().stream().map(k ->
                        {
                            Object val = toSerialize.get(k);
                            return Arrays.asList(MetaBotanyConfig.serialize(k, k.getClass()),
                                    MetaBotanyConfig.serialize(val, val.getClass()));
                        }
                ).collect(Collectors.toList());
            }

            @Override
            public Map<?, ?> deserialize(List<List<?>> toDeserialize, Type type) {
                HashMap<Object, Object> map = new HashMap<>();
                Type[] arguments = ((ParameterizedType) type).getActualTypeArguments();
                for(List<?> pair : toDeserialize) {
                    map.put(MetaBotanyConfig.deserialize(pair.get(0), arguments[0]),
                            MetaBotanyConfig.deserialize(pair.get(1), arguments[1]));
                }
                return map;
            }
        });
        serializers.put(Multimap.class, new ITomlSerializer<Multimap<Object, ?>, List<List<?>>>() {
            @Override
            public List<List<?>> serialize(Multimap<Object, ?> toSerialize) {
                return toSerialize.keySet().stream().map(k ->
                        {
                            List<?> val = new ArrayList<>(toSerialize.get(k));
                            return Arrays.asList(MetaBotanyConfig.serialize(k, k.getClass()),
                                    MetaBotanyConfig.serialize(val, val.getClass()));
                        }
                ).collect(Collectors.toList());
            }

            @Override
            public Multimap<Object, ?> deserialize(List<List<?>> toDeserialize, Type type) {
                HashMultimap<Object, Object> map = HashMultimap.create();
                Type[] arguments = ((ParameterizedType) type).getActualTypeArguments();
                for(List<?> pair : toDeserialize) {
                    map.putAll(MetaBotanyConfig.deserialize(pair.get(0), arguments[0]),
                            (Iterable<?>) pair.get(1));
                }
                return map;
            }
        });
        serializers.put(float.class, new ITomlSerializer<Float, Number>() {
            @Override
            public Number serialize(Float toSerialize) {
                return toSerialize.doubleValue();
            }

            @Override
            public Float deserialize(Number toDeserialize, Type type) {
                return toDeserialize.floatValue();
            }
        });
    }

    static {
        final Pair<Common, ForgeConfigSpec> commonPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = commonPair.getRight();
        COMMON = commonPair.getLeft();
        final Pair<Client, ForgeConfigSpec> clientPair = new ForgeConfigSpec.Builder().configure(Client::new);
        CLIENT_SPEC = clientPair.getRight();
        CLIENT = clientPair.getLeft();
        final Pair<Server, ForgeConfigSpec> serverPair = new ForgeConfigSpec.Builder().configure(Server::new);
        SERVER_SPEC = serverPair.getRight();
        SERVER = serverPair.getLeft();
    }

    @SuppressWarnings("unchecked")
    private static void addField(ModFileScanData.AnnotationData data,
                                 ForgeConfigSpec.Builder builder,
                                 List<Triple<Field, ForgeConfigSpec.ConfigValue<?>, Method>> fields) {
        String fieldName = data.getMemberName();
        String className = data.getClassType().getClassName();

        try {
            Class<?> clazz;
            try {
                clazz = Class.forName(className);
            } catch(Throwable t) {
                LOGGER.fatal("Something went wrong loading a class.", t);
                throw t;
            }

            Field field = clazz.getField(fieldName);
            Type fieldType = field.getGenericType();

            Map<String, Object> info = data.getAnnotationData();

            List<String> comment = (List<String>) info.getOrDefault("comment", new ArrayList<>());
            builder.comment(comment.toArray(new String[0]));

            builder.translation((String) info.getOrDefault("translation", ""));

            List<String> path = (List<String>) info.getOrDefault("path", new ArrayList<>());
            path.add(fieldName);
            ForgeConfigSpec.ConfigValue<Object> value = builder.define(path, serialize(field.get(null), fieldType));

            Method callback = null;
            String callbackName = (String) info.getOrDefault("callback", null);

            if(callbackName != null)
                try {
                    callback = clazz.getMethod(callbackName, String.class, field.getType());
                } catch(NoSuchMethodException ignored) {
                    LOGGER.warn("Couldn't find callback method, " + callbackName + " of: " + className + ". It may be private.");
                }

            fields.add(Triple.of(field, value, callback));

        } catch(NoSuchFieldException | ClassNotFoundException | IllegalAccessException | ExceptionInInitializerError e) {
            LOGGER.error("Setting configurable: " + className + "." + fieldName + " failed.", e);
        }
    }

    public static void loadConfig(List<Triple<Field, ForgeConfigSpec.ConfigValue<?>, Method>> fields) {
        for(Triple<Field, ForgeConfigSpec.ConfigValue<?>, Method> triple : fields) {
            try {
                triple.getLeft().set(null, deserialize(triple.getMiddle().get(), triple.getLeft().getGenericType()));
                if(triple.getRight() != null)
                    try {
                        triple.getRight().invoke(null, triple.getLeft().getName(), triple.getMiddle().get());
                    } catch(IllegalAccessException | InvocationTargetException e) {
                        LOGGER.warn("Callback on field \"" + triple.getLeft().getName() + "\" failed, method \"" + triple.getRight().getName() + "\" " +
                                (e instanceof InvocationTargetException ?
                                 "threw an exception." :
                                 "is not accessible."),
                                e instanceof InvocationTargetException ?
                                e.getCause() :
                                null);
                    }
            } catch(NullPointerException | IllegalAccessException e) {
                LOGGER.warn("Couldn't set " + triple.getLeft().getDeclaringClass().getName() + "." + triple.getLeft().getName() + " to " + triple.getMiddle().get() + " because it is " + (
                        e instanceof IllegalAccessException ?
                        "final" :
                        "not static") + ".");
            }
        }
    }

    @SubscribeEvent
    public static void onReload(ModConfig.ModConfigEvent evt) {
        switch(evt.getConfig().getType()) {
            case COMMON:
                loadConfig(Common.fields);
                break;
            case CLIENT:
                loadConfig(Client.fields);
                break;
            case SERVER:
                loadConfig(Server.fields);
        }
    }

    private static <C, T> Object serialize(C i, Type type) {
        if(type instanceof ParameterizedType)
            type = ((ParameterizedType) type).getRawType();
        @SuppressWarnings("unchecked")
        ITomlSerializer<C, T> serializer = (ITomlSerializer<C, T>) serializers.getOrDefault(type, ITomlSerializer.IDENTITY);
        return serializer.serialize(i);
    }

    private static <C, T> Object deserialize(T i, Type type) {
        Type rawType = type;
        if(type instanceof ParameterizedType)
            rawType = ((ParameterizedType) type).getRawType();
        @SuppressWarnings("unchecked")
        ITomlSerializer<C, T> serializer = (ITomlSerializer<C, T>) serializers.getOrDefault(rawType, ITomlSerializer.IDENTITY);
        return serializer.deserialize(i, type);
    }

    public static class Common {

        public static List<Triple<Field, ForgeConfigSpec.ConfigValue<?>, Method>> fields = new ArrayList<>();
        public static List<ModFileScanData.AnnotationData> data = new ArrayList<>();

        Common(ForgeConfigSpec.Builder builder) {
            builder.comment("Common configurations, synced from the server. Reloads on file saved.")
                    .push("common");

            for(ModFileScanData.AnnotationData a : data)
                addField(a, builder, fields);
        }

    }

    public static class Client {

        public static List<Triple<Field, ForgeConfigSpec.ConfigValue<?>, Method>> fields = new ArrayList<>();
        public static List<ModFileScanData.AnnotationData> data = new ArrayList<>();

        Client(ForgeConfigSpec.Builder builder) {
            builder.comment("Client-side configurations. Reloads on file saved.")
                    .push("client");

            for(ModFileScanData.AnnotationData a : data)
                addField(a, builder, fields);
        }

    }

    public static class Server {

        public static List<Triple<Field, ForgeConfigSpec.ConfigValue<?>, Method>> fields = new ArrayList<>();
        public static List<ModFileScanData.AnnotationData> data = new ArrayList<>();

        Server(ForgeConfigSpec.Builder builder) {
            builder.comment("Server-side configurations. Reloads on file saved.")
                    .push("server");

            for(ModFileScanData.AnnotationData a : data)
                addField(a, builder, fields);
        }

    }

}
