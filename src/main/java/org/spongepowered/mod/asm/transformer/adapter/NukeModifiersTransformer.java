package org.spongepowered.mod.asm.transformer.adapter;

import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PROTECTED;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import com.google.common.collect.ImmutableSet;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Iterator;
import java.util.Set;

// Makes everything in all minecraft classes public. Suitable for development environments, to avoid fiddling with mod ATs
// Inspired by Forge's AccessTransformer
public class NukeModifiersTransformer implements IClassTransformer {

    private static final boolean TRANSFORM_EVERYTHING = Boolean.parseBoolean(System.getProperty("sponge.debugTransform.all"));
    private static final Set<String> EXCLUSIONS = ImmutableSet.<String>builder()
            .add("net.minecraftforge.fml.common.asm")
            .build();

    @Override
    public byte[] transform(String name, String tranformedName, byte[] bytes) {
        if (!TRANSFORM_EVERYTHING && !tranformedName.startsWith("net.minecraft")) {
            return bytes;
        }

        for (String skip: EXCLUSIONS) {
            if (tranformedName.startsWith(skip)) {
                return bytes;
            }
        }

        String transformedSlash = tranformedName.replace('.', '/');

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);
        boolean isInterface = (classNode.access & ACC_INTERFACE) != 0;

        classNode.access = this.getAccess(classNode.access, true);

        for (FieldNode field : classNode.fields) {
            field.access = this.getAccess(field.access, !isInterface);
        }

        for (MethodNode method : classNode.methods) {

            method.access = this.getAccess(method.access, true);

            Iterator<AbstractInsnNode> it = method.instructions.iterator();
            while (it.hasNext()) {
                AbstractInsnNode node = it.next();
                if (node.getOpcode() == INVOKESPECIAL) {
                    MethodInsnNode insn = (MethodInsnNode) node;
                    if (!insn.name.equals("<init>") && insn.owner.equals(transformedSlash)) {
                        insn.setOpcode(INVOKEVIRTUAL);
                    }
                }
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    private int getAccess(int access, boolean removeFinal) {
        int val = (access | ACC_PUBLIC) & ~ACC_PRIVATE & ~ACC_PROTECTED;
        if (removeFinal) {
            val &= ~ACC_FINAL;
        }
        return val;
    }
}
