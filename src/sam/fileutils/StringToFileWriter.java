package sam.fileutils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

public class StringToFileWriter {

    private final int BUFFER_SIZE;

    public StringToFileWriter(int buffer_size) {
        BUFFER_SIZE = buffer_size;
    }
    /**
     * 
     * @param path
     * @param data
     * @param append if true, append to exiting file, else truncate the file
     */
    public void write(Path path, CharSequence data, Charset charset, boolean append, CodingErrorAction onMalformedInput,CodingErrorAction onUnmappableCharacter) throws IOException {
        EnumSet<StandardOpenOption> set = EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        set.add(append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING);

        try(FileChannel channel = FileChannel.open(path, set)) {
            CharBuffer chars = CharBuffer.wrap(data);
            CharsetEncoder encoder = charset.newEncoder()
                    .onMalformedInput(onMalformedInput)
                    .onUnmappableCharacter(onUnmappableCharacter);

            ByteBuffer bytes = ByteBuffer.allocate(BUFFER_SIZE);
            while(true) {
                CoderResult c = encoder.encode(chars, bytes, true);
                if(c.isUnderflow())
                    c = encoder.flush(bytes);

                if(c.isOverflow() || c.isUnderflow()) {
                    bytes.flip();
                    channel.write(bytes);
                    bytes.clear();
                }
                if(c.isUnderflow())
                    break;
                if((c.isUnmappable() && onUnmappableCharacter == CodingErrorAction.REPORT) || (c.isMalformed() && onMalformedInput == CodingErrorAction.REPORT))
                    c.throwException();
            }
        }
    }
    public void write(Path path, CharSequence data, boolean append) throws IOException {
        write(path, data, Charset.defaultCharset(), append, CodingErrorAction.REPORT, CodingErrorAction.REPORT);
    }
    public void write(Path path, CharSequence data, Charset charset, boolean append) throws IOException {
        write(path, data, charset, append, CodingErrorAction.REPORT, CodingErrorAction.REPORT);
    }
    public StringBuilder textOf(Path path, Charset charset, CodingErrorAction onMalformedInput,CodingErrorAction onUnmappableCharacter) throws IOException {
        StringBuilder sb = new StringBuilder();
        try(FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
            long size = channel.size();

            ByteBuffer bytes = ByteBuffer.allocate((int) (size < BUFFER_SIZE ? size + 10 : BUFFER_SIZE));
            CharBuffer chars = null;

            CharsetDecoder decoder = charset.newDecoder()
                    .onMalformedInput(onMalformedInput)
                    .onUnmappableCharacter(onUnmappableCharacter);


            while(true) {
                int n = channel.read(bytes);
                bytes.flip();

                if(n == -1) {
                    sb.append(decoder.decode(bytes));
                    break;
                }
                if(chars == null)
                    chars = CharBuffer.allocate((int)(bytes.capacity()/charset.newEncoder().maxBytesPerChar()) + 5);
                chars.clear();
                CoderResult c = decoder.decode(bytes,chars, false);

                if(c.isOverflow() || c.isUnderflow()) {
                    chars.flip();
                    sb.append(chars);
                }
                if((c.isUnmappable() && onUnmappableCharacter == CodingErrorAction.REPORT) || (c.isMalformed() && onMalformedInput == CodingErrorAction.REPORT))
                    c.throwException();

                bytes.compact();
            }
        }

        return sb;
    }
    public StringBuilder textOf(Path path, Charset charset) throws IOException {
        return  textOf(path, charset, CodingErrorAction.REPORT, CodingErrorAction.REPORT);
    }
    public StringBuilder textOf(Path path) throws IOException {
        return textOf(path, Charset.defaultCharset());
    }

    public void appendFileAtTop(byte[] data, Path target) throws IOException {
        Path temp = Files.createTempFile("appendFileAtTop-", "");
        try(OutputStream os = Files.newOutputStream(temp, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            os.write(data);
            if(Files.exists(target)) {
                Files.copy(target, os);
                Files.delete(target);   
            }
            Files.move(temp, target);
        }
    }
}
