package com.chasetech.pcount.Woosim;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.chasetech.pcount.R;
import com.woosim.printer.WoosimBarcode;
import com.woosim.printer.WoosimCmd;
import com.woosim.printer.WoosimImage;
import com.woosim.printer.WoosimService;

public class WoosimPrinter {

	private Context mContext;

    // Debugging

    // Message types sent from the BluetoothPrintService Handler
    public static final int MESSAGE_DEVICE_NAME = 1;
    public static final int MESSAGE_TOAST = 2;
    public static final int MESSAGE_READ = 3;

    // Key names received from the BluetoothPrintService Handler
    public static final String DEVICE_NAME = "WOOSIM";
    public static final String TOAST = "toast";
    
    // Layout Views
    
    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the print services
    private BluetoothPrintService mPrintService = null;
    private WoosimService mWoosim = null;

	public WoosimPrinter(Context ctx) {
		this.mContext = ctx;
		SetupPrinter();
	}

	private void SetupPrinter() {

		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(mContext, R.string.toast_bt_na, Toast.LENGTH_LONG).show();
			return;
		}

		mPrintService = new BluetoothPrintService(mContext, mHandler);
		mWoosim = new WoosimService(mHandler);
		this.Open();
	}

	public void Open() {
		mPrintService.start();
		this.connectDevice(true);
	}

	public void Close() {
		mPrintService.stop();
	}

    // The Handler that gets information back from the BluetoothPrintService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                //Toast.makeText(mContext, "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
				Log.wtf("Connected", "Connected to " + mConnectedDeviceName);
                break;
            case MESSAGE_READ:
                mWoosim.processRcvData((byte[])msg.obj, msg.arg1);
                break;
            }
        }
    };


    private void connectDevice(boolean secure) {
        // Get the device MAC address

		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

		String address = "";

		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {

				if (device.getName().equals(DEVICE_NAME)) { // NAME OF PAIRED BLUETOOTH PRINTER
					address = device.getAddress();
					break;
				}
			}
		}

        // Get the BLuetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mPrintService.connect(device, secure);
    }
    
    private boolean sendData(byte[] data) {
        // Check that we're actually connected before trying printing
        if (mPrintService.getState() != BluetoothPrintService.STATE_CONNECTED) {
            return false;
        }
        // Check that there's actually something to send
        if (data.length > 0) 
        	mPrintService.write(data);
		return true;
    }

    /**
     * On click function for sample print button. 
     */
    
    public boolean printBMPImage(Bitmap bmp, int x, int y, int width, int height) {

		boolean result = false;

    	if (bmp == null) {
    		Log.e("", "resource decoding is failed");
    		return result;
    	}

    	byte[] data = WoosimImage.printBitmap(x, y, width, height, bmp);
    	bmp.recycle();

    	if(sendData(WoosimCmd.setPageMode())) {
			if(sendData(data))
				result = sendData(WoosimCmd.PM_setStdMode());
		}

		return result;
    }

    public boolean printText(String strBody, boolean isBold, boolean isUnderlined, int charsize) throws IOException {

		boolean result = false;
    	byte[] text = null;

    	if (strBody.isEmpty()) {
			return result;
		}
    	else {
			try {
				text = strBody.getBytes("US-ASCII");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
    	}
    	ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    	byteStream.write(WoosimCmd.setTextStyle(isBold, isUnderlined, false, charsize, charsize));
    	byteStream.write(WoosimCmd.setTextAlign(WoosimCmd.ALIGN_LEFT));
    	byteStream.write(text);
    	byteStream.write(WoosimCmd.printData());
    	
    	if(sendData(WoosimCmd.initPrinter())) {
			result = sendData(byteStream.toByteArray());
		}

		return result;
    }
    
    /**
     * On click function for barcode print button. 
     * @throws IOException 
     */
    public void print1DBarcode(String barcodetype, String strBarcode) throws IOException {

		byte[] barcode = strBarcode.getBytes();
    	final byte[] cmd_print = WoosimCmd.printData();

		byte[] barcodeByte = null;

		switch (barcodetype) {
			case "EAN13":
				barcodeByte = WoosimBarcode.createBarcode(WoosimBarcode.EAN13, 2, 60, true, barcode);
				break;
			case "EAN8":
				barcodeByte = WoosimBarcode.createBarcode(WoosimBarcode.EAN8, 2, 60, true, barcode);
				break;
			default: // 128
				barcodeByte = WoosimBarcode.createBarcode(WoosimBarcode.CODE128, 2, 60, true, barcode);
				break;
		}
    	ByteArrayOutputStream byteStream = new ByteArrayOutputStream(512);
		byteStream.write(barcodeByte);
		byteStream.write(cmd_print);
    	
    	sendData(WoosimCmd.initPrinter());
    	sendData(byteStream.toByteArray());
    }
    
    public void print2DBarcode(View v) throws IOException {
    	final byte[] barcode = {0x30,0x31,0x32,0x33,0x34,0x35,0x36,0x37,0x38,0x39,0x30};
    	final byte[] cmd_print = WoosimCmd.printData();
    	final String title1 = "PDF417 2D Barcode\r\n";
    	byte[] PDF417 = WoosimBarcode.create2DBarcodePDF417(2, 3, 4, 2, false, barcode);
    	final String title2 = "DATAMATRIX 2D Barcode\r\n";
    	byte[] dataMatrix = WoosimBarcode.create2DBarcodeDataMatrix(0, 0, 6, barcode);
    	final String title3 = "QR-CODE 2D Barcode\r\n";
    	byte[] QRCode = WoosimBarcode.create2DBarcodeQRCode(0, (byte)0x4d, 5, barcode);
    	final String title4 = "Micro PDF417 2D Barcode\r\n";
    	byte[] microPDF417 = WoosimBarcode.create2DBarcodeMicroPDF417(2, 2, 0, 2, barcode);
    	final String title5 = "Truncated PDF417 2D Barcode\r\n";
    	byte[] truncPDF417 = WoosimBarcode.create2DBarcodeTruncPDF417(2, 3, 4, 2, false, barcode);
    	// Maxicode can be printed only with RX version
    	final String title6 = "Maxicode 2D Barcode\r\n";
    	final byte[] mxcode = {0x41,0x42,0x43,0x44,0x45,0x31,0x32,0x33,0x34,0x35,0x61,0x62,0x63,0x64,0x65};
    	byte[] maxCode = WoosimBarcode.create2DBarcodeMaxicode(4, mxcode);
    	
    	ByteArrayOutputStream byteStream = new ByteArrayOutputStream(512);
    	byteStream.write(title1.getBytes()); byteStream.write(PDF417); byteStream.write(cmd_print);
    	byteStream.write(title2.getBytes()); byteStream.write(dataMatrix); byteStream.write(cmd_print);
    	byteStream.write(title3.getBytes()); byteStream.write(QRCode); byteStream.write(cmd_print);
    	byteStream.write(title4.getBytes()); byteStream.write(microPDF417); byteStream.write(cmd_print);
    	byteStream.write(title5.getBytes()); byteStream.write(truncPDF417); byteStream.write(cmd_print);
    	byteStream.write(title6.getBytes()); byteStream.write(maxCode); byteStream.write(cmd_print);
    	
    	sendData(WoosimCmd.initPrinter());
    	sendData(byteStream.toByteArray());
    }

    public void printGS1Databar(View v) throws IOException {
    	final byte[] data = {0x30,0x30,0x30,0x31,0x32,0x33,0x34,0x35,0x36,0x37,0x38,0x39,0x30};
    	final byte[] cmd_print = WoosimCmd.printData();
    	final String title0 = "GS1 Databar type0\r\n";
    	byte[] gs0 = WoosimBarcode.createGS1Databar(0, 2, data);
    	final String title1 = "GS1 Databar type1\r\n";
    	byte[] gs1 = WoosimBarcode.createGS1Databar(1, 2, data);
    	final String title2 = "GS1 Databar type2\r\n";
    	byte[] gs2 = WoosimBarcode.createGS1Databar(2, 2, data);
    	final String title3 = "GS1 Databar type3\r\n";
    	byte[] gs3 = WoosimBarcode.createGS1Databar(3, 2, data);
    	final String title4 = "GS1 Databar type4\r\n";
    	byte[] gs4 = WoosimBarcode.createGS1Databar(4, 2, data);
    	final String title5 = "GS1 Databar type5\r\n";
    	final byte[] data5 = {0x5b,0x30,0x31,0x5d,0x39,0x30,0x30,0x31,0x32,0x33,0x34,0x35,0x36,0x37,0x38,0x39,0x30,0x38,
    						  0x5b,0x33,0x31,0x30,0x33,0x5d,0x30,0x31,0x32,0x32,0x33,0x33};
    	byte[] gs5 = WoosimBarcode.createGS1Databar(5, 2, data5);
    	final String title6 = "GS1 Databar type6\r\n";
    	final byte[] data6 = {0x5b,0x30,0x31,0x5d,0x39,0x30,0x30,0x31,0x32,0x33,0x34,0x35,0x36,0x37,0x38,0x39,0x30,0x38,
    						  0x5b,0x33,0x31,0x30,0x33,0x5d,0x30,0x31,0x32,0x32,0x33,0x33,
    						  0x5b,0x31,0x35,0x5d,0x39,0x39,0x31,0x32,0x33,0x31};
    	byte[] gs6 = WoosimBarcode.createGS1Databar(6, 4, data6);
    	
    	ByteArrayOutputStream byteStream = new ByteArrayOutputStream(512);
    	byteStream.write(title0.getBytes()); byteStream.write(gs0); byteStream.write(cmd_print);
    	byteStream.write(title1.getBytes()); byteStream.write(gs1); byteStream.write(cmd_print);
    	byteStream.write(title2.getBytes()); byteStream.write(gs2); byteStream.write(cmd_print);
    	byteStream.write(title3.getBytes()); byteStream.write(gs3); byteStream.write(cmd_print);
    	byteStream.write(title4.getBytes()); byteStream.write(gs4); byteStream.write(cmd_print);
    	byteStream.write(title5.getBytes()); byteStream.write(gs5); byteStream.write(cmd_print);
    	byteStream.write(title6.getBytes()); byteStream.write(gs6); byteStream.write(cmd_print);
    	
    	sendData(WoosimCmd.initPrinter());
    	sendData(byteStream.toByteArray());
    }
}
