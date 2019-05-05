package org.seckill.dao.cache;

import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisDao {
	private final Logger logger=LoggerFactory.getLogger(this.getClass());
	
	private JedisPool jedisPool;
	
	public RedisDao(String ip,int port) {
		jedisPool =new JedisPool(ip,port);
	}
	
	private RuntimeSchema<Seckill> schema=RuntimeSchema.createFrom(Seckill.class);
	
	public Seckill getSeckill(long seckillId) {
		// 缓存redis操作
		try {
			Jedis jedis=jedisPool.getResource();
			
			try {
				String key="seckill:"+seckillId;
				// 并没有实现内部序列化操作
				
				byte[] bytes=jedis.get(key.getBytes());
				if (bytes!=null) {
					Seckill seckill=schema.newMessage();
					//seckill反序列化操作
					ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);
					return seckill;
				} 
			} finally {
				jedis.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		} 
		
		
		return null;
	}
	
	public String putSeckill(Seckill seckill) {
		try {
			Jedis jedis=jedisPool.getResource();
			try {
				String key="seckill:"+seckill.getSeckillId();
				byte[] bytes=ProtostuffIOUtil.toByteArray(seckill, schema, 
						LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
				
				int timeout=60*60;//单位 秒
				String result=jedis.setex(key.getBytes(), timeout, bytes);
				return result;
			} finally {
				jedis.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}
	
}
