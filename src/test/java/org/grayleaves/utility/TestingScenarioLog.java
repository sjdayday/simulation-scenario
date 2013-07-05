package org.grayleaves.utility;

import java.util.ArrayList;
import java.util.List;

public class TestingScenarioLog<R, I> extends ScenarioLog<R, I> {

	public TestingScenarioLog() {
		super();
	}

	public TestingScenarioLog(Scenario<R, I> scenario) throws ScenarioException {
		this(); 
//		super(scenario);
	}
    @Override
    public void close() {
    }
    @Override
    public boolean error(String record) {
    	return true;
    }
    @Override
    public String getCustomData() {
    	return "test";
    }
    @Override
    public String getFilename() {
    	return "fname";
    }
    @Override
    public boolean getHasTrailer() {
    	return true;
    }
    @Override
    public int getRecordCount() {
    	return 1;
    }
    @Override
    public List<String> getRecords() throws ScenarioException {
    	return new ArrayList<String>();
    }
    @Override
    public boolean isClosed() {
    	return false;
    }
    @Override
    public boolean log(String record) {
    	return true;
    }
    @Override
    public void trailer() {
    }
    @Override
    public void trailer(String customData) {
    }
    
}
