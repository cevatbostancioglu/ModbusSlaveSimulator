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

import java.util.ArrayList;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.map.annotate.JsonRootName;

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
import com.serotonin.util.StringUtils;
import com.sun.javafx.scene.layout.region.Margins.Converter;
import com.sun.javafx.scene.paint.GradientUtils.Parser;

import jdk.nashorn.internal.runtime.regexp.JoniRegExp.Factory;


public class Simulator
{
	
	String 	settingsFilePath;
	File 	simulatorSettingsFile;
	SimulatorSettings simSettings;
	
	ModbusFactory mFactory;
	
	ModbusSlaveSet simulatedDevice;
	BasicProcessImage simulatedDeviceMap;
	
	private ArrayList<SlaveDevice> slaveDevices = new ArrayList<SlaveDevice>();
	
	ModbusLocator mLocator;
	
	IpParameters deviceIp;
	
	ObjectMapper mapper = new ObjectMapper();
	
	public String simInterface;
	public String simPort;
	public String simAddress;
	public String simProtocol;
	
 	Simulator(String jsonFilePath) throws IOException
	{
 		simulatorSettingsFile = new File(jsonFilePath);
 		byte[] encoded = Files.readAllBytes(Paths.get(jsonFilePath));
 		String jsonContentString = new String(encoded, StandardCharsets.UTF_8);
 
 		mFactory = new ModbusFactory();
 		simulatedDevice = mFactory.createTcpSlave(false);
		
 		simSettings = new SimulatorSettings();
 		
 		try
 		{
 			mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
 			simSettings = mapper.readValue(jsonContentString, SimulatorSettings.class);
 			
 			System.out.println(simSettings);
 			
 			mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
 			jsonContentString = mapper.writeValueAsString(simSettings);
 			
 			//System.out.println(jsonContentString);
 			
 			simSettings.printDeviceCodes();
 			
 			createSlaveDevice();
 		}
 		catch (JsonParseException e)
 		{e.printStackTrace();}
 		catch (JsonMappingException e)
 		{e.printStackTrace();}
 		catch (IOException e)
 		{e.printStackTrace();}
 		
	}
	
	public boolean startSimulator() throws ModbusTransportException, ErrorResponseException, ModbusInitException
	{
		for (SlaveDevice sDevice : slaveDevices) 
		{
			if(sDevice.getDeviceModbusProtocol().equals("TCP") && 
					simSettings.getTCP_SLAVES().equals("ENABLE"))
			{System.out.println("TCP_DeviceStarted");sDevice.startDevice();}
			else if(sDevice.getDeviceModbusProtocol().equals("UDP") && 
					simSettings.getUDP_SLAVES().equals("ENABLE"))
			{System.out.println("UDP");sDevice.startDevice();}
			else
			{System.out.println(sDevice.getDeviceModbusProtocol() + " Protocol not allowed. Check your register files or simulator settings.");}
		}

		return true;
	}
	
	private void createSlaveDevice() throws IOException
	{
		String currentDir = new java.io.File(".").getCanonicalPath();
			
		//currentDir = currentDir.replaceAll("\\", "\\");
			
		currentDir += "\\simulatorFiles" + "\\" + simSettings.getSLAVE_FILES_NAME() ;
			
		for(int i = 0; i < simSettings.getSlaveDeviceNumber(); i++)
		{
			File deviceSettingsFile = new File(currentDir + simSettings.getSlaveDeviceCode(i) + ".json");
				
			if(deviceSettingsFile.exists())
			{
				System.out.println("\n" + simSettings.getSlaveDeviceCode(i) + " Slave Devices installed.");
				SlaveDevice sDevice = new SlaveDevice(deviceSettingsFile.getAbsolutePath());
				slaveDevices.add(sDevice);
			}
			else
			{
				System.out.println(simSettings.getSlaveDeviceCode(i) + 
						" Slave Dosyasi bulunamadi. Dosya: " + deviceSettingsFile.getAbsolutePath());
			}
		}
		
		System.out.println("Toplam " + slaveDevices.size() + " adet cihaz olusturuldu.");
	}
	
}
