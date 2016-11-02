package SlaveSimulatorTest;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusTransportException;

import SlaveSimulator.Simulator;
import junit.framework.TestCase;

import java.io.File;

public class JsonFileTest
{
	File settingsFile;
	Simulator sim;	

	@Before
	public void setUp() throws Exception
	{
		settingsFile = new File("C:\\Users\\Cevat\\Google Drive\\cevat_private\\GitHub\\ModbusSlaveSimulator\\simulatorFiles\\SimulatorSettings.json");
		sim = new Simulator(settingsFile.getAbsolutePath());
	}

	@Test
	public void jsonParserTest() throws ModbusTransportException, ErrorResponseException
	{
		assertEquals(sim.simAddress, "1");
		assertEquals(sim.simInterface, "127.0.0.1");
		assertEquals(sim.simPort, "502");
		
		assertFalse(!(sim.simProtocol.contains("TCP") || sim.simProtocol.contains("UDP") || sim.simProtocol.contains("RTU")));
		
	}
	
	@Test
	public void startSimulatorTest() throws ModbusTransportException, ErrorResponseException
	{
		assertFalse((sim.startSimulator()));
		
	}

}
