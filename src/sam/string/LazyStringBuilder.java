package sam.string;

public interface LazyStringBuilder extends Appendable {
    public static LazyStringBuilder create(boolean create) {
        if(create) {
            return new LazyStringBuilder() {
                private final StringBuilder sb = new StringBuilder(); 
                @Override
                public LazyStringBuilder append(CharSequence csq) {
                    sb.append(csq);
                    return this;
                }
                @Override
                public LazyStringBuilder append(CharSequence csq, int start, int end) {
                    sb.append(csq, start, end);
                    return this;
                }
                @Override
                public LazyStringBuilder append(char c) {
                    sb.append(c);
                    return this;
                }
                @Override
                public LazyStringBuilder append(Object o) {
                    sb.append(o);
                    return this;
                }
                @Override
                public String toString() {
                    return sb.toString();
                }
                @Override
                public void setLength(int n) {
                    sb.setLength(n);
                }
            };
        } else {
            return new LazyStringBuilder() {
                
                @Override
                public LazyStringBuilder append(Object o) {
                    return this;
                }
                @Override
                public LazyStringBuilder append(CharSequence csq, int start, int end) {
                    return this;
                }
                @Override
                public LazyStringBuilder append(CharSequence csq) {
                    return this;
                }
                @Override
                public LazyStringBuilder append(char c) {
                    return this;
                }
                @Override
                public String toString() {
                    return "";
                }
                @Override
                public void setLength(int n) {
                }
            }; 
        }
    }
    
    LazyStringBuilder append(Object o);
    LazyStringBuilder append(CharSequence csq, int start, int end);
    LazyStringBuilder append(CharSequence csq);
    LazyStringBuilder append(char c) ;
    void setLength(int n) ;
    
}
