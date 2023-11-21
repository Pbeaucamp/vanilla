package bpm.vanilla.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;

import bpm.vanilla.platform.core.beans.resources.ISchedule;

@Configuration
public class VanillaRegisterModuleConfig {

	@Bean
	public Module scheduleEntityDeserializer() {
		SimpleModule module = new SimpleModule();
		module.addDeserializer(ISchedule.class, new ScheduleEntityDeserializer());
		return module;
	}
}