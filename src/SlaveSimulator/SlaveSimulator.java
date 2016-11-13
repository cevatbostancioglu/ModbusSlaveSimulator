package SlaveSimulator;

import java.io.File;
import java.io.IOException;

import com.serotonin.modbus4j.*;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.ip.IpParameters;
 

//http://infiniteautomation.com/forum/topic/1586/modbus4j-listenertest-tcp-java-net-bindexception
 
public class SlaveSimulator
{
	
	public static void main(String[] args) throws ModbusTransportException, ErrorResponseException, IOException, ModbusInitException
	{
		/*File settingsFile = new File("C:\\Users\\Cevat\\Google Drive\\cevat_private\\GitHub\\ModbusSlaveSimulator\\simulatorFiles\\slaveDevice-test.json");
		
		SlaveDevice sd = new SlaveDevice(settingsFile.getAbsolutePath());
		*/

		File settingsFile = new File("C:\\Users\\Cevat\\Google Drive\\cevat_private\\GitHub\\ModbusSlaveSimulator\\simulatorFiles\\SimulatorSettings.json");
		Simulator sim = new Simulator(settingsFile.getAbsolutePath());
		
		if(sim.startSimulator())
		{System.out.println("Simulation Started");}
		
		
	}

}
