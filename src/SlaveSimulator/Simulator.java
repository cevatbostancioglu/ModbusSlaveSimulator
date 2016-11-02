package SlaveSimulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
	
	JSONParser jParser = new JSONParser();

	public String simInterface;
	public String simPort;
	public String simAddress;
	public String simProtocol;
	
 	Simulator(String jsonFilePath)
	{
 		mFactory = new ModbusFactory();
 		simulatedDevice = mFactory.createTcpSlave(false);
		try
		{
			Object obj = jParser.parse(new FileReader(jsonFilePath));
			
			JSONObject jsonObject = (JSONObject) obj;
			
			String simName = (String) jsonObject.get("DeviceName");
			System.out.println("DeviceName :" + simName);
			
			simInterface  	= (String) jsonObject.get("DeviceModbusInterface");
			simPort	  	 	= (String) jsonObject.get("DeviceModbusPort");
			simAddress     	= (String) jsonObject.get("DeviceModbusAddress"); 			
			simProtocol    	= (String) jsonObject.get("DeviceModbusProtocol");
			
			System.out.println("ModbusSimulator Running on << " + simInterface + ":" + 
							simPort + " ID:" + simAddress + " Protocol:" + simProtocol + " >>");
			
		}
		catch (FileNotFoundException e)
		{	
			System.out.println("File Not Found Exception");
			e.printStackTrace();
		}
		catch (IOException e)
		{
			System.out.println("IO Exception");
			e.printStackTrace();
		}
		catch (ParseException e)
		{
			System.out.println("Parse Exception");
			e.printStackTrace();
		}
		
	}
	
	public boolean startSimulator() throws ModbusTransportException, ErrorResponseException
	{
		if(simInterface.contains("COM") || simInterface.contains("tty") 
				|| simInterface.contains("console") || simInterface.contains("amc"))
		{}
		else
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
