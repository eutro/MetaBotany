function initializeCoreMod() {
    var ASM = Java.type("net.minecraftforge.coremod.api.ASMAPI");
    var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");
    var InsnNode = Java.type("org.objectweb.asm.tree.InsnNode");
    var InsnList = Java.type("org.objectweb.asm.tree.InsnList");
    var Opcodes = Java.type("org.objectweb.asm.Opcodes");
    var MethodNode = Java.type("org.objectweb.asm.tree.MethodNode");
    var newInstructions = new InsnList();
    var HOOKS = "eutros/botaniapp/asm/ASMHooks";
    var COLLIDE_DESC = "(Lvazkii/botania/api/internal/IManaBurst;Lnet/minecraft/entity/projectile/ThrowableEntity;Lnet/minecraft/util/math/RayTraceResult;ZZLnet/minecraft/item/ItemStack;)Z"

    function harvestLevelTransformer(varIndex) { // index of the harvestLevel local variable
        return function(method) {
            newInstructions.add(new VarInsnNode(Opcodes.ILOAD, varIndex)); // int harvestLevel
            newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 6)); // ItemStack stack

            newInstructions.add(ASM.buildMethodCall(
                HOOKS,
                "manaLensEnchantSharper",
                "(ILnet/minecraft/item/ItemStack;)I",
                ASM.MethodType.STATIC
            ));

            newInstructions.add(new VarInsnNode(Opcodes.ISTORE, varIndex)); // int harvestLevel
            // harvestLevel = ASMHooks.manaLensEnchantSharper(harvestLevel, stack);

            var target = ASM.findFirstMethodCall(
                method,
                ASM.MethodType.VIRTUAL,
                "net/minecraftforge/common/ForgeConfigSpec$IntValue",
                "get",
                "()Ljava/lang/Object;"
            );

            var index = method.instructions.indexOf(target);
            target = ASM.findFirstInstructionAfter(method, Opcodes.ISTORE, index);

            method.instructions.insert(target, newInstructions); // Insert after the initial assignment of harvestLevel
            return method;
        }
    }

    function blockHardnessTransformer(method) {
        method = harvestLevelTransformer(13)(method);

        newInstructions.add(new VarInsnNode(Opcodes.FLOAD, 15)); // float hardness
        newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 6)); // ItemStack stack

        newInstructions.add(ASM.buildMethodCall(
            HOOKS,
            "manaLensEnchantSharperHardness",
            "(FLnet/minecraft/item/ItemStack;)F",
            ASM.MethodType.STATIC
        ));

        newInstructions.add(new VarInsnNode(Opcodes.FSTORE, 15)); // float hardness
        // hardness = ASMHooks.manaLensEnchantSharperHardness(hardness, stack);

        var target = ASM.findFirstMethodCall(
            method,
            ASM.MethodType.VIRTUAL,
            "net/minecraft/block/BlockState",
            "getBlockHardness", // getBlockHardness
            "(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;)F"
        );

        var index = method.instructions.indexOf(target);
        target = ASM.findFirstInstructionAfter(method, Opcodes.ISTORE, index);

        method.instructions.insert(target, newInstructions); // Insert after the initial assignment of harvestLevel

        return method;
    }

    var obj = {
        "bore-lens-hooks": {
            "target": {
                "type": "METHOD",
                "class": "vazkii.botania.common.item.lens.LensMine",
                "methodName": "collideBurst",
                "methodDesc": COLLIDE_DESC
            },
            "transformer": blockHardnessTransformer
        },
        "weight-lens-hooks": {
            "target": {
                "type": "METHOD",
                "class": "vazkii.botania.common.item.lens.LensWeight",
                "methodName": "collideBurst",
                "methodDesc": COLLIDE_DESC
            },
            "transformer": harvestLevelTransformer(7)
        },
        "lens-enchantability": {
            "target": {
                "type": "CLASS",
                "name": "vazkii.botania.common.item.lens.ItemLens",
            },
            "transformer": function(clazz) {
                var node = new MethodNode(1, // public
                                          "isEnchantable",
                                          "(Lnet/minecraft/item/ItemStack;)Z",
                                          null,
                                          []);

                node.instructions.add(new InsnNode(Opcodes.ICONST_1));
                node.instructions.add(new InsnNode(Opcodes.IRETURN));
                // return true;

                clazz.methods.add(node);

                node = new MethodNode(1, // public
                                      "getItemEnchantability",
                                      "(Lnet/minecraft/item/ItemStack;)I",
                                      null,
                                      []);

                node.instructions.add(new InsnNode(Opcodes.ICONST_5));
                node.instructions.add(new InsnNode(Opcodes.IRETURN));
                // return 5;

                clazz.methods.add(node);

                return clazz;
            }
        }
    }

    return obj;
}