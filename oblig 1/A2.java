class A2 {
    // descending sort a[v..k]


    // parallel (specified indexes)
    static void a2(int[] a, int v, int k, int h) {
        int kIndex = v + k - 1;
        insertSort(a, v, kIndex);
        sortElement(a, v, kIndex, h);
    }

    // for sequential
    static void a2(int[] a, int k) {
        insertSort(a, 0, k-1);
        sortElement(a, 0, k-1, a.length-1);
    }

    // descending sort a[v..h]
    static void insertSort(int[] a, int v, int h) {
        int i, t; 
        for (int k = v; k < h; k++) {
            t = a[k+1];
            i = k;
            while(i >= v && a[i] < t) {
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

    // bubble sort index h up till index v
    static void bubbleSort(int[]a, int v, int h) {
        while(h-1 >= v && a[h] > a[h-1]) {
            swap(a, h, --h);
        }
    }

    // 
    static void sortElement(int[] a, int v, int k, int h) {
        for (int j = k + 1; j <= h; j++) {
            if (a[j] > a[k]) {
                swap(a, k, j);
                // bubble sort the new element
                bubbleSort(a, v, k);
            }
        }
    }
}