package SlaveSimulator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Random;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonRootName;

import com.serotonin.modbus4j.BasicProcessImage;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusLocator;
import com.serotonin.modbus4j.ModbusSlaveSet;
import com.serotonin.modbus4j.code.RegisterRange;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.ip.IpParameters;

@JsonRootName("Registers")
class Registers
{
	static int totalRegisterNumber;
	
	@JsonProperty("Address")
	private int 	Address;
	
	@JsonProperty("InitFunction")
	private int 	initFunction;
	
	@JsonProperty("StartValue")
	private String 	startValue;
	
	@JsonProperty("Function")
	private String 	Function;
	
	public int getAddress() {
		return Address;
	}
	public void setAddress(int address) {
		Address = address;
	}
	public int getInitFunction() {
		return initFunction;
	}
	public void setInitFunction(int initFunction) {
		this.initFunction = initFunction;
	}
	public String getStartValue() {
		return startValue;
	}
	public void setStartValue(String startValue) {
		this.startValue = startValue;
	}
	public String getFunction() {
		return Function;
	}
	public void setFunction(String function) {
		Function = function;
	}
}

class SlaveDeviceFunctionsCode
{
	public static String Date		= "date";
	public static String Random 	= "random";
	public static String trueValue  = "true";
	public static String falseValue = "false";
	public static String On			= "on";
	public static String Off		= "off";
	
}

public class SlaveDevice 
{	
	private String 	deviceSettingsFilePath;
	private File	deviceSettingsFile;
	
	private String 	deviceName;
	private String 	deviceInterface;
	private int		devicePort;
	
	
	private int 	deviceModbusAddres;
	private String	deviceModbusProtocol;
	
	public int totalRegisterNumber;

	private ArrayList<Registers> registersArray = new ArrayList<Registers>();
	
	private ObjectMapper mapper = new ObjectMapper();
	private JsonNode registersNode,root;
	
	private ModbusFactory sFactory = new ModbusFactory();
	private ModbusSlaveSet sDevice;
	BasicProcessImage sDeviceMap;
	ModbusLocator sLocator;
	
	IpParameters sDeviceIP;
	
	SlaveDevice(String jsonFilePath) throws IOException
	{
		this.deviceSettingsFilePath = jsonFilePath;
		byte[] encoded = Files.readAllBytes(Paths.get(jsonFilePath));
		String jsonContentString = new String(encoded, StandardCharsets.UTF_8);
		
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		root = mapper.readTree(jsonContentString);
		
		mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
		
		deviceName = root.path("DeviceName").asText();
		deviceInterface = root.path("DeviceModbusInterface").asText();
		devicePort = root.path("DeviceModbusPort").asInt();
		deviceModbusAddres = root.path("DeviceModbusAddress").asInt();
		deviceModbusProtocol = root.path("DeviceModbusProtocol").asText();
		
		System.out.println("Name : " + deviceName);
		System.out.println("Interface : " + deviceInterface);
		System.out.println("DevicePort : " + devicePort);
		System.out.println("ModbusAddress : " + deviceModbusAddres);
		System.out.println("Protocol : " + deviceModbusProtocol);

		registersNode = root.path("Registers");
		
		
		
		for(JsonNode node: registersNode)
		{
			Registers reg = new Registers();
			
			reg.setAddress(node.path("Address").asInt());
			reg.setInitFunction((node.path("InitFunction").asInt()));
			reg.setStartValue(node.path("StartValue").asText());
			reg.setFunction(node.path("Function").asText());
			
			System.out.println("");
			
			System.out.println(reg.totalRegisterNumber + "->Adress       : " + reg.getAddress());
			System.out.println(reg.totalRegisterNumber + "->InitFunction : " + reg.getInitFunction());
			System.out.println(reg.totalRegisterNumber + "->StartValue   : " + reg.getStartValue());
			System.out.println(reg.totalRegisterNumber + "->Function     : " + reg.getFunction());
			System.out.println("");
			
			registersArray.add(reg);
			
			reg.totalRegisterNumber++;
			totalRegisterNumber++;
		}
		
		System.out.println("Cihazda " + totalRegisterNumber + " adet register acildi.\n");
	}

	public boolean startDevice() throws ModbusInitException
	{
		initializeRegisters();
		sDevice.start();
		
		return false;
	}
	
	private void initializeRegisters()
	{
		if(deviceInterface.contains("tty") || deviceInterface.contains("amc") || deviceInterface.contains("COM"))
		{
			System.out.println("RTU DEVICE");
		}
		else
		{
			System.out.println("MODBUS TCP/UDP");
			
			if(deviceModbusProtocol.equals("TCP"))
			{System.out.println("TCP device created");sDevice = sFactory.createTcpSlave(false);}
			else if(deviceModbusProtocol.equals("UDP"))
			{System.out.println("UDP device created");sDevice = sFactory.createUdpSlave(false);}
			
			sDeviceIP = new IpParameters();
			sDeviceIP.setHost(Integer.toString(deviceModbusAddres));
			sDeviceIP.setPort(devicePort);
			
			sDeviceMap = new BasicProcessImage(deviceModbusAddres);
			
			sDeviceMap.setAllowInvalidAddress(false);
			sDeviceMap.setInvalidAddressValue(Short.MIN_VALUE);
			
			for(int i=0; i < registersArray.size(); i++)
			{
				Registers regCurr = new Registers();
				
				regCurr = registersArray.get(i);
				
				if(regCurr.getInitFunction() == RegisterRange.COIL_STATUS) // 1
				{
					if(regCurr.getStartValue().toLowerCase().contains(SlaveDeviceFunctionsCode.Random))
					{
						Random rnd = new Random();
						Boolean rndValue = rnd.nextBoolean();
						
						sDeviceMap.setCoil(regCurr.getAddress(),rndValue);
						
						System.out.println("FN:1 -> " + regCurr.getAddress() + " -> random yuklendi -> " + rndValue);
					}
					else
					{
						sDeviceMap.setCoil(regCurr.getAddress(),regCurr.getStartValue().toLowerCase().
									equals(SlaveDeviceFunctionsCode.trueValue) ? true : false );
						
						System.out.println("FN:1 -> " + regCurr.getAddress() + " -> normal yuklendi -> " + regCurr.getStartValue());
					}
				}
				else if(regCurr.getInitFunction() == RegisterRange.INPUT_STATUS) // 2 
				{
					if(regCurr.getStartValue().toLowerCase().contains(SlaveDeviceFunctionsCode.On))
					{
						sDeviceMap.setInput(regCurr.getAddress(), true);
						
						System.out.println("FN:2 -> " + regCurr.getAddress() + " -> On yuklendi.");
					}
					else
					{
						sDeviceMap.setInput(regCurr.getAddress(), false);
						
						System.out.println("FN:2 -> " + regCurr.getAddress() + " -> Off yuklendi.");
					}
				}
				else if(regCurr.getInitFunction() == RegisterRange.HOLDING_REGISTER)
				{
					if(regCurr.getStartValue().toLowerCase().contains(SlaveDeviceFunctionsCode.Random))
					{
						Random rnd = new Random();
						int rndValue = rnd.nextInt();
						
						sDeviceMap.setHoldingRegister(regCurr.getAddress(), (short) (rndValue % Short.MAX_VALUE));
						
						System.out.println("FN:3 -> " + regCurr.getAddress() + " -> Random -> " +(short) (rndValue % Short.MAX_VALUE));
					}
					else
					{
						sDeviceMap.setHoldingRegister(regCurr.getAddress(),Short.parseShort(regCurr.getStartValue()));
						
						System.out.println("FN:3 -> " + regCurr.getAddress() + " -> " + Short.parseShort(regCurr.getStartValue()));
					}
				}
				else if(regCurr.getInitFunction() == RegisterRange.INPUT_REGISTER)
				{
					if(regCurr.getStartValue().toLowerCase().contains(SlaveDeviceFunctionsCode.Random))
					{
						Random rnd = new Random();
						int rndValue = rnd.nextInt();
						
						sDeviceMap.setInputRegister(regCurr.getAddress(), (short) (rndValue % Short.MAX_VALUE));
						
						System.out.println("FN:4 -> " + regCurr.getAddress() + " -> Random -> " +(short) (rndValue % Short.MAX_VALUE));
					}
					else
					{
						sDeviceMap.setInputRegister(regCurr.getAddress(),Short.parseShort(regCurr.getStartValue()));
						
						System.out.println("FN:4 -> " + regCurr.getAddress() + " -> " + Short.parseShort(regCurr.getStartValue()));
					}
				}
			}
			
			sDevice.addProcessImage(sDeviceMap);
		}
	}

	public void setTotalRegisterNumber(int totalRegisterNumber) {
		this.totalRegisterNumber = totalRegisterNumber;
	}


	@Override
	public String toString() {
		return "SlaveDevice [deviceSettingsFilePath=" + deviceSettingsFilePath + ", deviceSettingsFile="
				+ deviceSettingsFile + ", deviceName=" + deviceName + ", deviceInterface=" + deviceInterface
				+ ", devicePort=" + devicePort + ", deviceModbusAddres=" + deviceModbusAddres
				+ ", deviceModbusProtocol=" + deviceModbusProtocol + "]";
	}
	
	
	public String getDeviceSettingsFilePath() {
		return deviceSettingsFilePath;
	}

	public void setDeviceSettingsFilePath(String deviceSettingsFilePath) {
		this.deviceSettingsFilePath = deviceSettingsFilePath;
	}

	public File getDeviceSettingsFile() {
		return deviceSettingsFile;
	}

	public void setDeviceSettingsFile(File deviceSettingsFile) {
		this.deviceSettingsFile = deviceSettingsFile;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceInterface() {
		return deviceInterface;
	}

	public void setDeviceInterface(String deviceInterface) {
		this.deviceInterface = deviceInterface;
	}

	public int getDevicePort() {
		return devicePort;
	}

	public void setDevicePort(int devicePort) {
		this.devicePort = devicePort;
	}

	public int getDeviceModbusAddres() {
		return deviceModbusAddres;
	}

	public void setDeviceModbusAddres(int deviceModbusAddres) {
		this.deviceModbusAddres = deviceModbusAddres;
	}

	public String getDeviceModbusProtocol() {
		return deviceModbusProtocol;
	}

	public void setDeviceModbusProtocol(String deviceModbusProtocol) {
		this.deviceModbusProtocol = deviceModbusProtocol;
	}

	public int getTotalRegisterNumber() {
		return totalRegisterNumber;
	}
}
