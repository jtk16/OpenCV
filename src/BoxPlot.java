public class BoxPlot {
    public int minValIndex;
    public int lowerQuartileIndex;
    public int medianIndex;
    public int upperQuartileIndex;
    public int maxValIndex;

    public int min;
    public int lowerQuartile;
    public int median;
    public int upperQuartile;
    public int IQR;
    public int max;

    public int nonOutlierLength;

    public int[] data;

    public BoxPlot(int[] arr) {
        quickSort(arr, 0, arr.length-1);
        data = arr;

        medianIndex = arr.length/2;
        lowerQuartileIndex = medianIndex/2;
        upperQuartileIndex = medianIndex+lowerQuartileIndex;

        IQR = upperQuartileIndex-lowerQuartileIndex;
        minValIndex = Math.max((lowerQuartileIndex-(int)(1.5*IQR)), 0);
        maxValIndex = Math.min((upperQuartileIndex+(int)(1.5*IQR)), data.length-1);

        nonOutlierLength = maxValIndex - minValIndex;

        min = data[min];
        lowerQuartile = data[lowerQuartileIndex];
        median = data[medianIndex];
        upperQuartile = data[upperQuartileIndex];
        max = data[maxValIndex];
    }

    public static void quickSort(int[] arr, int start, int end) {

        int partition = partition(arr, start, end);

        if(partition-1>start) {
            quickSort(arr, start, partition - 1);
        }
        if(partition+1<end) {
            quickSort(arr, partition + 1, end);
        }
    }

    public static int partition(int[] arr, int start, int end){
        int pivot = arr[end];

        for(int i=start; i<end; i++){
            if(arr[i]<pivot){
                int temp= arr[start];
                arr[start]=arr[i];
                arr[i]=temp;
                start++;
            }
        }

        int temp = arr[start];
        arr[start] = pivot;
        arr[end] = temp;

        return start;
    }

    @Override
    public String toString() {
        String out = "";
        for (int i = 0; i < data.length; i++) {
            out += data[i] + " ";
        }

        out += "\nminIndex: " + minValIndex + ", minValue: " + min + "\n";
        out += "maxIndex: " + maxValIndex + ", maxValue: " + max + "\n";


        return out;
    }
}
