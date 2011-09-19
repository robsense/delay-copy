package org.gnu.dc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;

public class Program {

	public static void main(String[] args) {
		try {
			File f = new File(args[0]);
			String s = displayBytes(BigInteger.valueOf(f.length()));
			FileInputStream fi = new FileInputStream(args[0]);
			FileOutputStream fo = new FileOutputStream(args[1]);

			byte[] buffer = new byte[2*1024*1024]; // 0.5 MB

			long m1 = System.currentTimeMillis();
			BigInteger data = new BigInteger("0");

			double hks = Double.parseDouble(args[2])*10;
			long d = (long)(f.length() / 1024L / 1024L / Double.parseDouble(args[2]));
			BigInteger hk = BigInteger.valueOf(1024 * 100);
			while (true) {
				int read = fi.read(buffer, 0, buffer.length);
				if (read > -1) {
					fo.write(buffer, 0, read);
					fo.flush();
					data = data.add(BigInteger.valueOf(read));
					long secs = (System.currentTimeMillis() - m1) / 1000;
					if (secs == 0) {
						Thread.sleep(1000);
					} else {
						double rate = 2 * hks;
						while (rate > hks) {
							secs = (System.currentTimeMillis() - m1) / 1000;
							rate = data.divide(BigInteger.valueOf(secs))
									.divide(hk).doubleValue();
							// rate = 100k/sec
							if (rate > hks) {
//								System.out.println("Too fast... is " + ((int)rate)/10
//										+ " MB/s (data " + data + ") time=" + secs+"s");
								Thread.sleep(500);
							} else {
								String p = "unkown";
								String tmp = displayBytes(data);
								// 200 10 5% 10*100
								p= String.format("%03d", data.multiply(BigInteger.valueOf(100)).divide(BigInteger.valueOf(f.length())).longValue());
								String output = tmp + " copied ("+p+"%, "+s+" bytes total, remaining "+(d-secs)+"s)";
								System.out.print("\r" + output);
								System.out.flush();
							}
						}
					}

				} else {
					break;
				}
			}

			fi.close();
			fo.flush();
			fo.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public static String displayBytes(BigInteger src) {
		String tmp = "";
		BigInteger tmpInt = src.multiply(BigInteger.ONE);
		while (tmpInt.compareTo(BigInteger.ZERO) > 0) {
			tmp = String.format("%03d", tmpInt.mod(BigInteger.valueOf(1000)).longValue()) + "." + tmp;
			tmpInt = tmpInt.divide(BigInteger.valueOf(1000));
		}
		tmp = tmp.replaceAll("^\\.|\\.$", "");
		if (tmp.charAt(0) == '0') tmp = tmp.substring(1);
		return tmp;
	}

}
