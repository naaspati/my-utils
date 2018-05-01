package sam.tsv;

class Escaper {
    private StringBuilder default_buffer = new StringBuilder();
    private volatile boolean inUse = false;

    private StringBuilder getBuffer() {
        if(inUse)
            return new StringBuilder();
        else {
            inUse = true;
            default_buffer.setLength(0);
            return default_buffer;
        }
    }
    private void setBuffer(StringBuilder sb) {
        if(sb == default_buffer)
            inUse = false;
    }
    String[] escape(String[] strings) {
        if(strings == null || strings.length == 0)
            return strings;

        for (int i = 0; i < strings.length; i++) 
            strings[i] = escape(strings[i]);

        return strings;    
    }
    String escape(String string) {
        StringBuilder buffer = getBuffer();

        try {
            if(string == null || string.isEmpty() || (string.indexOf('\r') == -1 && string.indexOf('\n') == -1 && string.indexOf('\t') == -1))
                return string;

            for (int i = 0; i < string.length(); i++) {
                char c = string.charAt(i);

                if(c == '\r' || c == '\n' || c == '\t')
                    buffer.append(c == '\r' ? "\\r" : c == '\n' ? "\\n" : "\\t");
                else
                    buffer.append(c);
            }
            return buffer.toString();
        } finally {
            setBuffer(buffer);
        }


    }
    String[] unescape(String strings[]) {
        if(strings == null || strings.length == 0)
            return strings; 

        for (int i = 0; i < strings.length; i++)
            strings[i] = unescape(strings[i]);
        return strings;
    }
    String unescape(String string) {
        StringBuilder buffer = getBuffer();

        try {
            if(string == null || string.length() < 2 || string.indexOf('\\') == -1)
                return string;

            buffer.append(string.charAt(0));

            for (int j = 1; j < string.length(); j++) {
                char c = string.charAt(j);
                if((c == 'n' || c == 'r' || c == 't') && countPrecedingBackwordSlashes(string, j)%2 != 0)
                    buffer.setCharAt(buffer.length() - 1, c == 'n' ? '\n' : c == 'r' ? '\r' : '\t');
                else
                    buffer.append(c);
            }
            return buffer.toString();
        } finally {
            setBuffer(buffer);
        }
    }
    int countPrecedingBackwordSlashes(String string, int startAt) {
        if(string.charAt(startAt - 1) == '\\') {
            int i = 2;
            while(string.charAt(startAt - i) == '\\') {i++;};
            return i - 1;
        }
        else
            return 0;
    }

}
