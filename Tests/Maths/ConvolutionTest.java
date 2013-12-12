package Maths;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: sebastianzillessen
 * Date: 10.11.13
 * Time: 12:17
 * To change this template use File | Settings | File Templates.
 */
public class ConvolutionTest {
    @Test
    public void testGetGaussianKernel1D() throws Exception {
        double[] res = Convolution.getGaussianKernel1D(1, 1);
        assertEquals("[0.274068619061197, 0.45186276187760605, 0.274068619061197]", Arrays.toString(res));
    }

    @Test
    public void sumshouldbe1() throws Exception {
        double[] res = Convolution.getGaussianKernel1D(1, 1);
        double sum = 0;
        for (int i = 0; i < res.length; i++) {
            sum += res[i];
        }
        assertEquals(1.0, sum, 0.0000001);
    }


    @Test
    public void testGetGaussianKernel2DSumShouldBe1() throws Exception {
        double[][] res = Convolution.getGaussianKernel2D(1, 1);
        double sum = 0;
        for (int i = 0; i < res.length; i++) {
            for (int j = 0; j < res.length; j++) {
                sum += res[i][j];
            }
        }
        assertEquals(1.0, sum, 0.0000001);
    }

    @Test
    public void convolution2D() {

        AbstractMatrix m = AbstractMatrix.parse(
                "8.2257 0.0000 -5.0876 4.4050 0.3241 -8.6348 0.0000 0.0000 -5.0900 0.6847\n" +
                        "2.0567 8.2741 9.3821 1.3325 -7.3518 1.6772 -9.2738 1.5473 -0.5456 -6.4957\n" +
                        "8.7106 9.7430 -6.4342 -9.6503 -5.7576 -0.7078 3.6786 0.0000 0.0000 9.7825\n" +
                        "8.8769 -5.8098 -0.6058 0.0000 -2.5560 -4.5552 6.8247 -8.0657 -7.4463 -4.6523\n" +
                        "5.6042 0.7132 -1.0264 -1.5127 0.0000 1.9136 -7.3247 2.2848 -4.5179 -0.4741\n" +
                        "9.7991 -0.5969 -8.9251 2.4179 6.9808 3.3163 2.4732 9.8109 -2.1853 -0.4665\n" +
                        "2.1681 4.5260 7.9360 -5.8931 3.1818 6.8568 5.8963 -3.4809 7.2467 -6.3222\n" +
                        "-0.2096 -3.3234 -1.7047 -6.2254 0.0000 1.5220 8.8677 5.1959 -8.4538 -3.8859\n" +
                        "2.7897 -8.7851 0.0000 -2.6035 7.5576 4.3032 -4.4150 8.1122 7.2167 -4.7831\n" +
                        "7.6927 7.1806 9.9243 -3.0519 -4.4386 1.5277 -0.0831 -5.2062 0.0000 -3.0693");
        System.out.println(m);
        double[][] gaussianKernel2D = Convolution.getGaussianKernel2D(2, 0.8);
        UnitHelper.assertArrayEquals(AbstractMatrix.parse(
                " 0.0005 0.0050 0.0109 0.0050 0.0005\n" +
                        " 0.0050 0.0522 0.1141 0.0522 0.0050\n" +
                        " 0.0109 0.1141 0.2491 0.1141 0.0109\n" +
                        " 0.0050 0.0522 0.1141 0.0522 0.0050\n" +
                        " 0.0005 0.0050 0.0109 0.0050 0.0005").toArray(), gaussianKernel2D, 0.001);
        double[][] conv = Convolution.convolute(m.toArray(), gaussianKernel2D);
        System.out.println(new Matrix(conv));
        AbstractMatrix c = AbstractMatrix.parse(
                " 2.8482 2.0672 0.8052 0.6050 -1.2544 -2.7511 -1.9320 -1.0052 -1.5062 -1.0645\n" +
                        " 4.0092 4.7260 2.3206 -0.8300 -2.9556 -2.6627 -2.1433 -0.9511 -0.9593 -0.8276\n" +
                        " 4.7825 3.9460 -0.3976 -3.6051 -3.8470 -1.6247 -0.1939 -0.9030 -0.7865 0.6708\n" +
                        " 3.8519 1.1381 -1.5215 -2.3315 -2.2838 -1.1082 -0.1279 -2.2184 -3.1702 -1.3438\n" +
                        " 3.4053 0.6529 -1.4511 -0.6600 0.4494 0.3391 -0.2141 -0.5652 -2.1434 -1.6139\n" +
                        " 3.5180 1.2334 -1.2015 0.0645 2.7161 3.1153 2.5613 2.4115 0.2115 -1.0712\n" +
                        " 2.0299 1.6415 0.2821 -0.7089 1.9946 4.2557 4.2392 2.4573 0.2011 -1.8023\n" +
                        " 0.1578 -0.8050 -1.1479 -1.7080 1.0037 3.3505 4.0763 2.7289 -0.5580 -2.4110\n" +
                        " 0.8393 -0.5881 -0.4334 -0.6650 1.3283 2.1350 1.6604 2.3597 0.8783 -1.5490\n" +
                        " 2.6837 2.8464 2.3694 -0.0416 -0.2634 0.5176 -0.1434 -0.1964 -0.0517 -1.0320");
        assertEquals(c.toString(), new Matrix(conv).toString());
    }


}
