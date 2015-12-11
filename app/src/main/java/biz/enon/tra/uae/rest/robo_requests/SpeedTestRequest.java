package biz.enon.tra.uae.rest.robo_requests;

import com.octo.android.robospice.request.SpiceRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by mobimaks on 07.09.2015.
 */
public final class SpeedTestRequest extends SpiceRequest<Long> {

//    private static final String FILE_URL = "http://www.hdwallpapersllc.com/wp-content/uploads/2015/07/7001768-android-phone-logo-wallpapers.jpg";
    private static final String FILE_URL = "https://drive.google.com/uc?export=download&id=0B1GU18BxUf8hTFdUcFpMejlYUXc";
    private static final int CONNECTION_TIMEOUT_MILLIS = 15 * 1000;

    public SpeedTestRequest() {
        super(Long.class);
    }

    @Override
    public Long loadDataFromNetwork() throws Exception {
        final int count = 1;
        final long[] speedResults = new long[count];
        for (int i = 0; i < count; i++) {
            final long kbPerSecond = calculateDownloadingSpeed();
            speedResults[i] = kbPerSecond;
        }
        long speedSum = 0;
        for (long speedResult : speedResults) {
            speedSum += speedResult;
        }

        return speedSum / count;
    }

    private long calculateDownloadingSpeed() throws IOException {
        long startTime = System.nanoTime();

        final URL url = new URL(FILE_URL);
        final URLConnection urlConnection;
        urlConnection = url.openConnection();
        urlConnection.setConnectTimeout(CONNECTION_TIMEOUT_MILLIS);
        urlConnection.setUseCaches(false);
        urlConnection.connect();

        InputStream is = urlConnection.getInputStream();
        byte data[] = new byte[1024];

        long totalByteSize = 0;
        int byteCount;

        while ((byteCount = is.read(data)) != -1) {
            totalByteSize += byteCount;
        }
        double downloadTimeInSec = (System.nanoTime() - startTime) * Math.pow(10, -9);
        is.close();
        return Math.round(totalByteSize / 1024 / downloadTimeInSec);
    }

}
