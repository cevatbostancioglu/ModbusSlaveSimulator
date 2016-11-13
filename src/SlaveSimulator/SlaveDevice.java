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
	public static String Date = "date";
	public static String Random = "random";
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

	private ArrayList<Registers> registers;
	
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
		
		registers = new ArrayList<Registers>();
		
		registersNode = root.path("Registers");
		
		Registers reg = new Registers();
		
		for(JsonNode node: registersNode)
		{
			reg.setAddress(node.path("Address").asInt());
			reg.setInitFunction((node.path("InitFunciton").asInt()));
			reg.setStartValue(node.path("StartValue").asText());
			reg.setFunction(node.path("Function").asText());
			
			System.out.println("");
			
			System.out.println(reg.totalRegisterNumber + "->Adress       : " + reg.getAddress());
			System.out.println(reg.totalRegisterNumber + "->InitFunction : " + reg.getInitFunction());
			System.out.println(reg.totalRegisterNumber + "->StartValue   : " + reg.getStartValue());
			System.out.println(reg.totalRegisterNumber + "->Function     : " + reg.getFunction());
			System.out.println("");
			
			registers.add(reg);
			
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
			
			Registers reg = new Registers();
			
			for(int i=0; i < registers.size(); i++)
			{
				reg = registers.get(i);
				
				if(reg.getInitFunction() == RegisterRange.COIL_STATUS)
				{
					if(reg.getStartValue().toLowerCase().equals(SlaveDeviceFunctionsCode.Random))
					{
						Random rnd = new Random();
						sDeviceMap.setCoil(reg.getAddress(), true);
						System.out.println("random yuklendi.");
					}
					else
					{sDeviceMap.setCoil(reg.getAddress(),reg.getStartValue().toLowerCase().equals("true") ? true : false );}
				}
				/*else if(reg.getInitFunction() == )
				{
					RegisterRange.
				}
				*/
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
