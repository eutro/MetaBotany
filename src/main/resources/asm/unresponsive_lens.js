function initializeCoreMod() {
    var ASM = Java.type("net.minecraftforge.coremod.api.ASMAPI");
    var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");
    var IntInsnNode = Java.type("org.objectweb.asm.tree.IntInsnNode");
    var InsnList = Java.type("org.objectweb.asm.tree.InsnList");
    var Opcodes = Java.type("org.objectweb.asm.Opcodes");
    var newInstructions = new InsnList();
    var HOOKS = "eutros/botaniapp/asm/ASMHooks";
    var COLLIDE_DESC = "(Lvazkii/botania/api/internal/IManaBurst;Lnet/minecraft/entity/projectile/ThrowableEntity;Lnet/minecraft/util/math/RayTraceResult;ZZLnet/minecraft/item/ItemStack;)Z"

    function instanceOfTransformer(descMap, burstIndex){
        return function(method) {
            var index = 0;
            while(true) {
                instruction = ASM.findFirstInstructionAfter(method, Opcodes.INSTANCEOF, index);
                if(instruction == null) {
                    return method;
                }
                index = method.instructions.indexOf(instruction) + 1;

                if(descMap[instruction.desc] != null) {
                    newInstructions.add(new VarInsnNode(Opcodes.ALOAD, burstIndex));
                    newInstructions.add(ASM.buildMethodCall(
                        "vazkii/botania/api/internal/IManaBurst",
                        "getSourceLens",
                        "()Lnet/minecraft/item/ItemStack;",
                        ASM.MethodType.INTERFACE
                    ));
                    // [${burstIndex}].getSourceLens()

                    newInstructions.add(new IntInsnNode(Opcodes.BIPUSH, descMap[instruction.desc]))
                    // ${descMap[instruction.desc]}

                    newInstructions.add(ASM.buildMethodCall(
                        HOOKS,
                        "instanceOfHook",
                        "(Ljava/lang/Object;Lnet/minecraft/item/ItemStack;I)Z",
                        ASM.MethodType.STATIC
                    ));
                    // ASMHooks.instanceOfHook(test, [${burstIndex}].getSourceLens(), ${descMap[instruction.desc]})

                    method.instructions.insert(instruction, newInstructions);
                    method.instructions.remove(instruction);
                    // replace the instanceof instruction
                }
            }
        }
    }

    var obj = {
        "burst-collision-hook": {
            "target": {
                "type": "METHOD",
                "class": "vazkii.botania.common.entity.EntityManaBurst",
                "methodName": "onImpact",
                "methodDesc": "(Lnet/minecraft/util/math/RayTraceResult;)V"
            },
            "transformer": instanceOfTransformer({"net/minecraft/block/LeavesBlock": 0,
                                                  "vazkii/botania/api/mana/IManaTrigger": 1,
                                                  "vazkii/botania/api/mana/IManaReceiver": 2},
                                                  "(Ljava/lang/Object;Lnet/minecraft/item/ItemStack;I)Z",
                                                  0) // this
        },
        "damage-lens-hook": {
            "target": {
                "type": "METHOD",
                "class": "vazkii.botania.common.item.lens.LensDamage",
                "methodName": "updateBurst",
                "methodDesc": "(Lvazkii/botania/api/internal/IManaBurst;Lnet/minecraft/entity/projectile/ThrowableEntity;Lnet/minecraft/item/ItemStack;)V"
            },
            "transformer": instanceOfTransformer({"net/minecraft/entity/player/PlayerEntity": 3},
                                                  1) // burst
        },
        "bore-lens-hook": {
            "target": {
                "type": "METHOD",
                "class": "vazkii.botania.common.item.lens.LensMine",
                "methodName": "collideBurst",
                "methodDesc": COLLIDE_DESC
            },
            "transformer": instanceOfTransformer({"vazkii/botania/api/mana/IManaBlock": 4},
                                                  1) // burst
        }
    }

    return obj;
}