package SlaveSimulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import org.json.*;

import com.serotonin.modbus4j.*;
import com.serotonin.modbus4j.BasicProcessImage;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.code.RegisterRange;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.msg.ModbusRequest;
import com.serotonin.modbus4j.msg.ModbusResponse;
import com.sun.javafx.scene.layout.region.Margins.Converter;
import com.sun.javafx.scene.paint.GradientUtils.Parser;

import jdk.nashorn.internal.runtime.regexp.JoniRegExp.Factory;

public class Simulator
{
	
	String 	settingsFilePath;
	File 	simulatorSettingsFile;
	
	ModbusFactory mFactory;
	
	ModbusSlaveSet simulatedDevice;
	BasicProcessImage simulatedDeviceMap;
	
	
	ModbusLocator mLocator;
	
	IpParameters deviceIp;
	
	private JSONObject jsonObject;
	
	public String simInterface;
	public String simPort;
	public String simAddress;
	public String simProtocol;
	
 	Simulator(String jsonFilePath) throws JSONException, IOException
	{
 		simulatorSettingsFile = new File(jsonFilePath);
 		byte[] encoded = Files.readAllBytes(Paths.get(jsonFilePath));
 		String jsonContentString = new String(encoded, StandardCharsets.UTF_8);
 		
 		mFactory = new ModbusFactory();
 		simulatedDevice = mFactory.createTcpSlave(false);
		jsonObject = new JSONObject(jsonContentString);
		
		String simName = (String) jsonObject.get("DeviceName");
		
		System.out.println("DeviceName :" + simName);
		
		simInterface  	= (String) jsonObject.get("DeviceModbusInterface");
		simPort	  	 	= (String) jsonObject.get("DeviceModbusPort");
		simAddress     	= (String) jsonObject.get("DeviceModbusAddress"); 			
		simProtocol    	= (String) jsonObject.get("DeviceModbusProtocol");
		
		System.out.println("ModbusSimulator Running on << " + simInterface + ":" + 
						simPort + " ID:" + simAddress + " Protocol:" + simProtocol + " >>");
		
	}
	
	public boolean startSimulator() throws ModbusTransportException, ErrorResponseException
	{
		if(simInterface.contains("COM") || simInterface.contains("tty") 
				|| simInterface.contains("console") || simInterface.contains("amc"))
		{}
		else //if(simInter)
		{
			deviceIp = new IpParameters();
			deviceIp.setHost(simAddress);
			deviceIp.setPort(Integer.parseInt(simPort));
			
			simulatedDeviceMap = new BasicProcessImage(Integer.parseInt(simAddress));
			
			if(simProtocol.equals("TCP"))
			{
				//simulatedDevice = mFactory.createTcpSlave(false);
				
				simulatedDeviceMap.setCoil(1,  false);
				simulatedDeviceMap.setCoil(2,  false);
				simulatedDeviceMap.setCoil(3,  true);
				simulatedDeviceMap.setCoil(4,  true);
				simulatedDeviceMap.setCoil(5,  false);
				simulatedDeviceMap.setCoil(6,  false);
				simulatedDeviceMap.setCoil(7,  false);
				simulatedDeviceMap.setCoil(8,  true);
				simulatedDeviceMap.setCoil(9,  true);
				simulatedDeviceMap.setCoil(10, false);
				simulatedDeviceMap.setCoil(11, false);
				simulatedDeviceMap.setCoil(12, false);
				simulatedDeviceMap.setCoil(13, true);
				simulatedDeviceMap.setCoil(14, true);
				simulatedDeviceMap.setCoil(15, false);
				simulatedDevice.addProcessImage(simulatedDeviceMap);
				
				try
				{
					simulatedDevice.start();
				}
				catch (ModbusInitException e)
				{
					e.printStackTrace();
				}
				return true;
			}
			
		}
		
		return false;
	}
	
	
	
	
	
}
