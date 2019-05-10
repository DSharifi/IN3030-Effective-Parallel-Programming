class IntList{
    int[] data;
    int len = 0;

    IntList(int len) {
        data = new int[Math.max(2, len)];
    }

    IntList() {
        data = new int[16]; // some default size
    }

    void add(int elem) {
        if (len == data.length) { // dynamically expand array
            int[] b = new int[data.length * 2];
            System.arraycopy(data, 0, b, 0, len);
            // for (int i = 0; i < len; i++) b[i] = data[i];
            data = b;
        }
        data[len++] = elem;
    } // end add

    // adds IntList other to this IntList as last part
    void append(IntList other) {
        if (len + other.len > data.length) {
            int newLen = Math.max(2 * len, len + 2 * other.len);
            int[] b = new int[newLen];
            System.arraycopy(data, 0, b, 0, len);
            data = b;
        }
        System.arraycopy(other.data, 0, data, len, other.len);
        len += other.len;
    } // end join other Intlist to this IntList

    void clear() {
        len = 0;
    } // end clear;





    int get(int pos) {
        if (pos > len - 1)
            return -1;
        else
            return data[pos];
    } // end get

    int size() {
        return len;
    } // end size

} // end class IntList