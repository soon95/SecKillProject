package org.seckill.service.impl;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/spring-dao.xml", "classpath:spring/spring-service.xml" })
public class SeckillServiceImplTest {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SeckillService SeckillService;

	@Test
	public void testGetSeckillList() {
		List<Seckill> list = SeckillService.getSeckillList();
		logger.info("list={}", list);
	}

	@Test
	public void testGetById() {
		long seckillId = 1005;
		Seckill seckill = SeckillService.getById(seckillId);
		logger.info("seckill={}", seckill);
	}
	/*
	 * seckill=Seckill [seckillId=1005, name=800元秒杀ipad, number=200, startTime=Tue
	 * Jan 01 08:00:00 CST 2019, endTime=Tue Oct 01 08:00:00 CST 2019,
	 * createTime=Thu May 02 23:48:51 CST 2019]
	 */

	@Test
	public void testExportSeckillUrl() {
		long id = 1004;
		Exposer exposer = SeckillService.exportSeckillUrl(id);
		logger.info("exposer={}", exposer);
	}
	/*
	 * exposer=Exposer [exposed=true, md5=62fccdcec7ee55af1fabf6145e12d687,
	 * seckillId=1005, now=0, start=0, end=0] exposer=Exposer [exposed=true,
	 * md5=b85b6f1df88db4422019a9c97164b01d, seckillId=1004, now=0, start=0, end=0]
	 */

	@Test
	public void testExecuteSeckill() {
		long id = 1004;
		long phone = 15850164578L;
		String md5 = "b85b6f1df88db4422019a9c97164b01d";
		try {
			SeckillExecution execution = SeckillService.executeSeckill(id, phone, md5);
			logger.info("execution={}", execution);
		} catch (RepeatKillException | SeckillCloseException e) {
			logger.error(e.getMessage());
		}
	}

	@Test
	public void testSeckillLogic() {
		long id = 1007;
		Exposer exposer = SeckillService.exportSeckillUrl(id);
		if (exposer.isExposed()) {
			logger.info("exposer={}", exposer);
			long phone = 15850164578L;
			String md5 = exposer.getMd5();

			try {
				SeckillExecution execution = SeckillService.executeSeckill(id, phone, md5);
				logger.info("execution={}", execution);
			} catch (RepeatKillException | SeckillCloseException e) {
				logger.error(e.getMessage());
			}
		} else {
			// 秒杀未开启
			logger.warn("exposer={}", exposer);
		}

	}

}
