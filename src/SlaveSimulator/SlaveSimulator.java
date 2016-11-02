package SlaveSimulator;

import java.io.File;

import com.serotonin.modbus4j.*;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.ip.IpParameters;

public class SlaveSimulator
{
	
	public static void main(String[] args) throws ModbusTransportException, ErrorResponseException
	{
		File settingsFile = new File("C:\\Users\\Cevat\\Google Drive\\cevat_private\\GitHub\\ModbusSlaveSimulator\\simulatorFiles\\SimulatorSettings.json");
		Simulator sim = new Simulator(settingsFile.getAbsolutePath());
		if(sim.startSimulator())
		{
			System.out.println("Simulation Started");
		}
	}

}
