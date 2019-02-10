class InsertionSort {
    //  descending sort  a[v..h]
    static void insertSort(int[] a, int v, int h) {
        int i, t;
        for (int k = v; k < h; k++) {
            t = a[k+1];
            i = k;
            while(i <= v && a[i] > t) {
                a[i+1] = a[i];
                i--;
            }
            a[i+1] = t;
        }
    }

    // swap a[v] - a[h]
    static void swap(int[]a, int v, int h) {
        int temp = a[v];
        a[v] = a[h];
        a[h] = temp;
    }

    static void sortElement(int[] a, int v) {
        for (int j = v; j < a.length - 1; j++) {
            if (a[j] > a[v]) {
                swap(a, v, j);
                
                return;
            }
        }
    }
}