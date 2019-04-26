/**
 * Copyright (c) 2004-2011 QOS.ch
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package sam.logging;

import java.util.function.Supplier;
// import sam.logging.SamPrefixLogFilter;

/**
 * copy paste of org.slf4j.Logger
 * @author Sameer
 *
 */
public interface Logger {
	static final LoggerManager manager = (new Supplier<LoggerManager>() {
		@Override
		public LoggerManager get() {
			String cls = System.getProperty(LoggerManager.class.getName());
			if(cls == null)
				cls = System.getenv(LoggerManager.class.getName());
			if(cls != null) {
				try {
					return (LoggerManager) Class.forName(cls).newInstance();
				} catch (Throwable e) {
					throw new RuntimeException(cls, e);
				}
			}
			
			if(System.getProperty("java.util.logging.config.file") != null)
			return new DefaultLoggerManager();
			else {
				Logger log = new Logger() {
					public boolean isDebugEnabled(){ return false; }
					public void debug(String msg){}
					public void debug(Supplier<String> msgSupplier){}
					public void debug(String format, Object arg){}
					public void debug(String format, Object arg1, Object arg2){}
					public void debug(String format, Object... arguments){}
					public void debug(String msg, Throwable t){}

					public boolean isInfoEnabled(){return false; }
					public void info(String msg){}
					public void info(Supplier<String> msgSupplier){}
					public void info(String format, Object arg){}
					public void info(String format, Object arg1, Object arg2){}
					public void info(String format, Object... arguments){}
					public void info(String msg, Throwable t){}

					public boolean isWarnEnabled(){return false; }
					public void warn(String msg){}
					public void warn(String format, Object arg){}
					public void warn(String format, Object... arguments){}
					public void warn(String format, Object arg1, Object arg2){}
					public void warn(String msg, Throwable t){}

					public boolean isErrorEnabled(){ return false; }
					public void error(String msg){}
					public void error(String format, Object arg){}
					public void error(String format, Object arg1, Object arg2){}
					public void error(String format, Object... arguments){}
					public void error(String msg, Throwable t){}
				};
				
				return s -> log;
			}
		}
	}).get();  

	static Logger getLogger(@SuppressWarnings("rawtypes") Class cls) {
		return manager.get(cls); 
	}

	boolean isDebugEnabled();
	void debug(String msg);
	void debug(Supplier<String> msgSupplier);
	void debug(String format, Object arg);
	void debug(String format, Object arg1, Object arg2);
	void debug(String format, Object... arguments);
	void debug(String msg, Throwable t);

	boolean isInfoEnabled();
	void info(String msg);
	void info(Supplier<String> msgSupplier);
	void info(String format, Object arg);
	void info(String format, Object arg1, Object arg2);
	void info(String format, Object... arguments);
	void info(String msg, Throwable t);

	boolean isWarnEnabled();
	void warn(String msg);
	void warn(String format, Object arg);
	void warn(String format, Object... arguments);
	void warn(String format, Object arg1, Object arg2);
	void warn(String msg, Throwable t);

	boolean isErrorEnabled();
	void error(String msg);
	void error(String format, Object arg);
	void error(String format, Object arg1, Object arg2);
	void error(String format, Object... arguments);
	void error(String msg, Throwable t);

}
