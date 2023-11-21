package bpm.vanilla.api;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import bpm.vanilla.platform.core.beans.resources.ISchedule;
import bpm.workflow.commons.beans.Schedule;

class ScheduleEntityDeserializer extends JsonDeserializer<ISchedule> {
	
	@Override
	public ISchedule deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
	    return mapper.readValue(p, Schedule.class);
	}
}