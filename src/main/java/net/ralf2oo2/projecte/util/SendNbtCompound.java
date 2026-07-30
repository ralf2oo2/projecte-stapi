package net.ralf2oo2.projecte.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;

import java.io.*;

public class SendNbtCompound {
    public static void writeNbtCompound(NbtCompound tag, DataOutputStream data) throws IOException {
        if (tag == null)
        {
            data.writeShort(-1);
        }
        else
        {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            NbtIo.writeCompressed(tag, byteStream);
            byte[] bytes = byteStream.toByteArray();
            data.writeShort((short)bytes.length);
            data.write(bytes);
        }
    }

    public static NbtCompound readNbtCompound(DataInputStream data) throws IOException {
        short length = data.readShort();
        if (length < 0)
        {
            return null;
        }
        else
        {
            byte[] bytes = new byte[length];
            data.readFully(bytes);
            return NbtIo.readCompressed(new ByteArrayInputStream(bytes));
        }
    }
}
