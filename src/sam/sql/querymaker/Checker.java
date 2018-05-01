package sam.sql.querymaker;

abstract class Checker {
    protected void checkArray(int[] values) {
        if(values == null || values.length == 0)
            throw new IllegalArgumentException("invalid data");
    }
    protected void checkArray(double[] values) {
        if(values == null || values.length == 0)
            throw new IllegalArgumentException("invalid data");
    }
    protected void checkArray(float[] values) {
        if(values == null || values.length == 0)
            throw new IllegalArgumentException("invalid data");
    }
    protected void checkArray(char[] values) {
        if(values == null || values.length == 0)
            throw new IllegalArgumentException("invalid data");
    }
    protected void checkArray(long[] values) {
        if(values == null || values.length == 0)
            throw new IllegalArgumentException("invalid data");
    }
    protected <E> void checkArray(E[] values) {
        if(values == null || values.length == 0)
            throw new IllegalArgumentException("invalid data");
    }
}
