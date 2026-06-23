package fr.anatom3000.gwwhit.kizzyjune;

import fr.anatom3000.gwwhit.config.ConfigManager;
import io.gitlab.jfronny.libjf.unsafe.asm.AsmConfig;
import io.gitlab.jfronny.libjf.unsafe.asm.patch.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Map;
import java.util.Set;

// This is based on the quicmäth bytecode 🏳️‍⚧️former by JFronny!
public class Transformer implements AsmConfig {
    private static final String MATH_OWNER = "java/lang/Math";
    private static final String MATH_UTIL_OWNER = "fr/anatom3000/gwwhit/kizzyjune/MathUtil69";

    private static final Map<String, Boolean> STAT = Map.ofEntries(
            Map.entry("sin", true),
            Map.entry("cos", true)
    );

    private record Replacement(String owner, String name, String desc) {}

    @Override
    public Set<String> skipClasses() {
        return null;
    }

    @Override
    public Set<Patch> getPatches() {
        return Set.of(this::patchInvokes);
    }

    private boolean patchInvokes(ClassNode cn) {
        boolean changed = false;

        if (!ConfigManager.getActiveConfig().misc.breakMath) {
            return false;
        }

        // I'm an engineer btw

        for (MethodNode method : cn.methods) {
            for (AbstractInsnNode insn : method.instructions.toArray()) {
                if (insn instanceof LdcInsnNode ldc) {
                    if (ldc.cst instanceof Double dVal) {
                        if (dVal == Math.PI) {
                            ldc.cst = 4D;
                            changed = true;
                        } else if (dVal == Math.PI / 180.0D) {
                            ldc.cst = Math.PI / 90.0D;
                            changed = true;
                        }
                    } else if (ldc.cst instanceof Float fVal) {
                        if (fVal == (float) Math.PI) {
                            ldc.cst = 4F;
                            changed = true;
                        } else if (fVal == (float) Math.PI / 180.0F) {
                            ldc.cst = (float) Math.PI / 90.0F;
                            changed = true;
                        }
                    }
                }

                if (insn instanceof MethodInsnNode mIns) {
                    int opcode = mIns.getOpcode();
                    if (opcode != Opcodes.INVOKESTATIC && opcode != Opcodes.INVOKEVIRTUAL && opcode != Opcodes.INVOKEINTERFACE) {
                        continue;
                    }

                    Replacement insNew = null;
                    if (MATH_OWNER.equals(mIns.owner) && STAT.containsKey(mIns.name)) {
                        insNew = new Replacement(MATH_UTIL_OWNER, mIns.name, mIns.desc);
                    }

                    if (insNew != null && Boolean.TRUE.equals(STAT.get(insNew.name))) {
                        String replacementSimple = insNew.owner.substring(insNew.owner.lastIndexOf('/') + 1);
                        String classSimple = cn.name.contains("/") ? cn.name.substring(cn.name.lastIndexOf('/') + 1) : cn.name;
                        if (replacementSimple.equals(classSimple)) continue;

                        if (!insNew.desc.equals(mIns.desc)) {
                            throw new IllegalStateException("Descriptor mismatch for method " + insNew.name + ": expected " + insNew.desc + ", found " + mIns.desc);
                        }

                        if (mIns.getOpcode() != Opcodes.INVOKESTATIC) {
                            Type[] params = Type.getArgumentTypes(mIns.desc);
                            if (params.length > 1) {
                                throw new IllegalArgumentException("The quickmäth (yes this is a quickmäth fork) bytecode transformer does not support more than one argument");
                            }
                            for (Type param : params) {
                                if (param.getSize() != 1) {
                                    throw new IllegalStateException("The quickmäth (yes this is a quickmäth fork) bytecode transformer only supports category 1 computational types");
                                }
                            }
                            if (params.length == 1) {
                                method.instructions.insertBefore(mIns, new InsnNode(Opcodes.SWAP));
                            }
                            method.instructions.insertBefore(mIns, new InsnNode(Opcodes.POP));
                        }

                        mIns.setOpcode(Opcodes.INVOKESTATIC);
                        mIns.owner = MATH_UTIL_OWNER;
                        mIns.name = insNew.name;
                        mIns.itf = false;

                        changed = true;
                    }
                }
            }
        }
        return changed;
    }
}
