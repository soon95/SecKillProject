package org.seckill.exception;


/**
 * 秒杀关闭异常
 * @author Soon
 *
 */
public class SeckillCloseException extends SeckillException{

	public SeckillCloseException(String message, Throwable cause) {
		super(message, cause);
	}

	public SeckillCloseException(String message) {
		super(message);
	}
	
	
	
}
