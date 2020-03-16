package eutros.botaniapp.common.config;

import eutros.botaniapp.api.internal.config.Configurable;
import eutros.botaniapp.api.internal.config.ITomlSerializer;
import eutros.botaniapp.common.utils.Reference;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid=Reference.MOD_ID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class BotaniaPPConfig {

    private static final Logger LOGGER = LogManager.getLogger();

    private static Map<Class<?>, ITomlSerializer<?, ?>> serializers = new HashMap<>();

    public static class Common {

        public static List<Triple<Field, ForgeConfigSpec.ConfigValue<?>, Method>> fields = new ArrayList<>();

        Common(ForgeConfigSpec.Builder builder) {
            builder.comment("Common configurations, synced from the server. Reloads on file saved.")
                    .push("common");

            org.objectweb.asm.Type annotationType = org.objectweb.asm.Type.getType(Configurable.class);
            List<ModFileScanData> allScanData = ModList.get().getAllScanData();

            for (ModFileScanData scanData : allScanData) {
                Iterable<ModFileScanData.AnnotationData> annotations = scanData.getAnnotations();
                for (ModFileScanData.AnnotationData a : annotations) {
                    if (Objects.equals(a.getAnnotationType(), annotationType)) {
                        String fieldName = a.getMemberName();
                        String className = a.getClassType().getClassName();

                        try {
                            Class<?> clazz = Class.forName(className);
                            Field field = clazz.getField(fieldName);
                            Type fieldType = field.getGenericType();

                            Map<String, Object> info = a.getAnnotationData();

                            @SuppressWarnings("unchecked")
                            List<String> comment = (List<String>) info.getOrDefault("comment", new ArrayList<>());
                            builder.comment(comment.toArray(new String[0]));

                            builder.translation((String) info.getOrDefault("translation", ""));

                            @SuppressWarnings("unchecked")
                            List<String> path = (List<String>) info.getOrDefault("path", new ArrayList<>());
                            path.add(fieldName);
                            ForgeConfigSpec.ConfigValue<Object> value = builder.define(path, serialize(field.get(null), fieldType));

                            Method callback = null;
                            String callbackName = (String) info.getOrDefault("callback", null);

                            if (callbackName != null)
                                try {
                                    callback = clazz.getMethod(callbackName, String.class, field.getType());
                                } catch (NoSuchMethodException ignored) {}

                            fields.add(Triple.of(field, value, callback));

                        } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException | ExceptionInInitializerError e) {
                            LOGGER.error("Setting configurable: " + className + "." + fieldName + " failed.", e);
                        }
                    }
                }
            }
        }
    }

    static {
        serializers.put(Set.class, new ITomlSerializer<Set<?>, List<?>>() {
            @Override
            public List<?> serialize(Set<?> toSerialize) {
                return toSerialize.stream().map(i -> BotaniaPPConfig.serialize(i, i.getClass())).collect(Collectors.toList());
            }

            @Override
            public Set<?> deserialize(List<?> toDeserialize, Type type) {
                return toDeserialize.stream().map(i -> BotaniaPPConfig.deserialize(i, ((ParameterizedType) type).getActualTypeArguments()[0]))
                        .collect(Collectors.toSet());
            }
        });
    }

    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
        loadConfig(Common.fields);
    }

    public static void loadConfig(List<Triple<Field, ForgeConfigSpec.ConfigValue<?>, Method>> fields) {
        for(Triple<Field, ForgeConfigSpec.ConfigValue<?>, Method> triple : fields) {
            try {
                triple.getLeft().set(null, deserialize(triple.getMiddle().get(), triple.getLeft().getGenericType()));
                if(triple.getRight() != null)
                    triple.getRight().invoke(null, triple.getLeft().getName(), triple.getMiddle().get());
            } catch (IllegalAccessException | NullPointerException | InvocationTargetException e) {
                LOGGER.error("Couldn't set " + triple.getLeft().getDeclaringClass().getName() + "." + triple.getLeft().getName() + " to " + triple.getMiddle().get() + "because it is " + (e instanceof IllegalAccessException ? "final" : "not static") + ".");
            }
        }
    }

    @SubscribeEvent
    public static void onReload(ModConfig.Reloading evt) {
        switch(evt.getConfig().getType()) {
            case COMMON:
                loadConfig(Common.fields);
                break;
            case CLIENT:
            case SERVER:
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
}
