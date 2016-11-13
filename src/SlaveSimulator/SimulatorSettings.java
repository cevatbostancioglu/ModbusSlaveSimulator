package SlaveSimulator;

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonProperty;

class SimulatorSettings
{
	
	@JsonProperty("TCP_SLAVES")
	private String TCP_SLAVES;
	
	@JsonProperty("UDP_SLAVES")
	private String UDP_SLAVES;
	
	@JsonProperty("SERIAL_SLAVES")
	private String SERIAL_SLAVES;
	
	@JsonProperty("SLAVE_FILES_NAME")
	private String SLAVE_FILES_NAME;
	
	@JsonProperty("PRINT_TCP_REQ")
	private String PRINT_TCP_REQ;
	
	@JsonProperty("PRINT_UDP_REQ")
	private String PRINT_UDP_REQ;
	
	@JsonProperty("PRINT_SERIAL_REQ")
	private String PRINT_SERIAL_REQ;
	
	@JsonProperty("LOG_TCP_REQ")
	private String LOG_TCP_REQ;
	
	@JsonProperty("LOG_UDP_REQ")
	private String LOG_UDP_REQ;
	
	@JsonProperty("LOG_RTU_REQ")
	private String LOG_RTU_REQ;
	
	@JsonProperty("ENABLED_SLAVES")
	private String ENABLED_SLAVES;

	@Override
	public String toString() {
		return "SimulatorSettings [TCP_SLAVES=" + TCP_SLAVES + ", UDP_SLAVES=" + UDP_SLAVES + ", SERIAL_SLAVES="
				+ SERIAL_SLAVES + ", SLAVE_FILES_NAME=" + SLAVE_FILES_NAME + ", PRINT_TCP_REQ=" + PRINT_TCP_REQ
				+ ", PRINT_UDP_REQ=" + PRINT_UDP_REQ + ", PRINT_SERIAL_REQ=" + PRINT_SERIAL_REQ + ", LOG_TCP_REQ="
				+ LOG_TCP_REQ + ", LOG_UDP_REQ=" + LOG_UDP_REQ + ", LOG_RTU_REQ=" + LOG_RTU_REQ + ", ENABLED_SLAVES="
				+ ENABLED_SLAVES + "]";
	}
	
	public String getTCP_SLAVES() {
		return TCP_SLAVES;
	}
	
	public void setTCP_SLAVES(String TCP_SLAVES) {
		this.TCP_SLAVES = TCP_SLAVES;
	}
	
	public String getUDP_SLAVES() {
		return UDP_SLAVES;
	}
	
	public void setUDP_SLAVES(String UDP_SLAVES) {
		this.UDP_SLAVES = UDP_SLAVES;
	}
	
	public String getSERIAL_SLAVES() {
		return SERIAL_SLAVES;
	}
	
	public void setSERIAL_SLAVES(String SERIAL_SLAVES) {
		this.SERIAL_SLAVES = SERIAL_SLAVES;
	}
	
	public String getSLAVE_FILES_NAME() {
		return SLAVE_FILES_NAME;
	}
	
	public void setSERIAL_FILES_NAME(String SLAVE_FILES_NAME) {
		this.SLAVE_FILES_NAME = SLAVE_FILES_NAME;
	}
	
	public String getPRINT_TCP_REQ() {
		return PRINT_TCP_REQ;
	}
	
	public void setPRINT_TCP_REQ(String PRINT_TCP_REQ) {
		this.PRINT_TCP_REQ = PRINT_TCP_REQ;
	}
	
	public String getPRINT_UDP_REQ() {
		return PRINT_UDP_REQ;
	}
	
	public void setPRINT_UDP_REQ(String PRINT_UDP_REQ) {
		this.PRINT_UDP_REQ = PRINT_UDP_REQ;
	}
	
	public String getPRINT_SERIAL_REQ() {
		return PRINT_SERIAL_REQ;
	}
	
	public void setPRINT_SERIAL_REQ(String PRINT_SERIAL_REQ) {
		this.PRINT_SERIAL_REQ = PRINT_SERIAL_REQ;
	}
	
	public String getLOG_TCP_REQ() {
		return LOG_TCP_REQ;
	}
	
	public void setLOG_TCP_REQ(String LOG_TCP_REQ) {
		this.LOG_TCP_REQ = LOG_TCP_REQ;
	}
	
	public String getLOG_UDP_REQ() {
		return LOG_UDP_REQ;
	}
	
	public void setLOG_UDP_REQ(String LOG_UDP_REQ) {
		this.LOG_UDP_REQ = LOG_UDP_REQ;
	}
	
	public String getLOG_RTU_REQ() {
		return LOG_RTU_REQ;
	}
	
	public void setLOG_RTU_REQ(String LOG_RTU_REQ) {
		this.LOG_RTU_REQ = LOG_RTU_REQ;
	}
	
	public String getENABLED_SLAVES() {
		return ENABLED_SLAVES;
	}

	public void setENABLED_SLAVES(String ENABLED_SLAVES) {
		this.ENABLED_SLAVES = ENABLED_SLAVES;
	}
	
	public int getSlaveDeviceNumber()
	{
		return (ENABLED_SLAVES.length() - ENABLED_SLAVES.replace(",", "").length() + 1);
	}
	
	public String getSlaveDeviceCode(int deviceNumber)
	{
		
		String deviceList[] = ENABLED_SLAVES.split(",");
		
		return deviceList[deviceNumber]; 
	}
	
	public void printDeviceCodes()
	{
		for(int i=0; i < getSlaveDeviceNumber(); i++)
		System.out.println(i + ".device -> " + getSlaveDeviceCode(i));
	}
	
}
