function initializeCoreMod() {
    var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
    var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
    var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
    var Opcodes = Java.type('org.objectweb.asm.Opcodes');
    var newInstructions = new InsnList();

    function harvestLevelTransformer(varIndex) { // index of the harvestLevel local variable
        return function(method) {
            newInstructions.add(new VarInsnNode(Opcodes.ILOAD, varIndex)) // int harvestLevel
            newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 6)) // ItemStack stack

            newInstructions.add(ASM.buildMethodCall(
                "eutros/botaniapp/asm/ASMHooks",
                "manaLensEnchantSharper",
                "(ILnet/minecraft/item/ItemStack;)I",
                ASM.MethodType.STATIC
            ));

            newInstructions.add(new VarInsnNode(Opcodes.ISTORE, varIndex)) // int harvestLevel

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
    return {
        'bore-lens-hooks': {
            'target': {
                'type': 'METHOD',
                'class': 'vazkii.botania.common.item.lens.LensMine',
                'methodName': 'collideBurst',
                'methodDesc': '(Lvazkii/botania/api/internal/IManaBurst;Lnet/minecraft/entity/projectile/ThrowableEntity;Lnet/minecraft/util/math/RayTraceResult;ZZLnet/minecraft/item/ItemStack;)Z'
            },
            'transformer': function(method) {
                method = harvestLevelTransformer(13)(method);

                newInstructions.add(new VarInsnNode(Opcodes.FLOAD, 15)) // float hardness
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 6)) // ItemStack stack

                newInstructions.add(ASM.buildMethodCall(
                    "eutros/botaniapp/asm/ASMHooks",
                    "manaLensEnchantSharperHardness",
                    "(FLnet/minecraft/item/ItemStack;)F",
                    ASM.MethodType.STATIC
                ));

                newInstructions.add(new VarInsnNode(Opcodes.FSTORE, 15)) // float hardness

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
        },
        'weight-lens-hooks': {
            'target': {
                'type': 'METHOD',
                'class': 'vazkii.botania.common.item.lens.LensWeight',
                'methodName': 'collideBurst',
                'methodDesc': '(Lvazkii/botania/api/internal/IManaBurst;Lnet/minecraft/entity/projectile/ThrowableEntity;Lnet/minecraft/util/math/RayTraceResult;ZZLnet/minecraft/item/ItemStack;)Z'
            },
            'transformer': harvestLevelTransformer(7)
        }
    }
}