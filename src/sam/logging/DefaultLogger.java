package sam.logging;

import java.lang.ref.WeakReference;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.LogRecord;

class DefaultLogger implements Logger {
	private final java.util.logging.Logger logger;

	public DefaultLogger(java.util.logging.Logger logger) {
		this.logger = logger;
	}
	public boolean isEnabled(Level level){
		return logger.isLoggable(level);
	} 

	public void log(Level level, String msg, Object arg) {
		if(!isEnabled(level))
			return;
		
		msg = convert(msg);

		if(arg instanceof Throwable) 
			logger.log(level, msg, (Throwable)arg);
		else 
			logger.log(level, msg, new Object[]{arg});
	}
	public void log(Level level, String format, Object...args) {
		if(!isEnabled(level))
			return;
		
		format = convert(format);

		Throwable t  = null;
		if(args.length > 0 && args[args.length - 1] instanceof Throwable )
			t = (Throwable) args[args.length - 1];

		LogRecord lr = new LogRecord(level, format);
		lr.setParameters(args);
		if(t != null)
			lr.setThrown(t);
		
		lr.setLoggerName(logger.getName());
		
		final ResourceBundle  bundle = logger.getResourceBundle();
		final String ebname = logger.getResourceBundleName();
		
		if (ebname != null && bundle != null) {
			lr.setResourceBundleName(ebname);
			lr.setResourceBundle(bundle);
		}
		
		logger.log(lr);
	}

	private static WeakReference<StringBuilder> wsb = new WeakReference<StringBuilder>(null);
	private static final Object LOCK = new Object();

	private String convert(String s) {
		int end = s.indexOf('{');
		int start = 0;
		if(end < 0)
			return s;

		synchronized (LOCK) {
			int k = 0;
			StringBuilder sb = null;
			if(wsb == null || (sb = wsb.get()) == null)
				wsb = new WeakReference<>(sb = new StringBuilder());

			int found = 0;
			while(end > 0) {
				sb.append(s, start, end + 1);
				if(s.length() > end + 1 && s.charAt(end + 1) == '}') {
					sb.append(k++);
					found++;
				}

				start = end + 1;
				end = s.indexOf('{', start);
				if(end < 0) {
					sb.append(s, start, s.length());
					break;
				}
			}

			if(found == 0) {
				sb.setLength(0);
				return s;
			} else {
				s = sb.toString();
				sb.setLength(0);
				return s;
			}
		}
	}

	@Override
	public void info(String msg) {
		logger.info(msg);
	}
	@Override
	public boolean isInfoEnabled() {
		return isEnabled(Level.INFO);
	}
	@Override
	public void info(String format, Object arg) {
		log(Level.INFO, format, arg);
	}
	@Override
	public void info(String format, Object arg1, Object arg2) {
		log(Level.INFO, format, arg1, arg2);
	}
	@Override
	public void info(String format, Object... arguments) {
		log(Level.INFO, format, arguments);
	}
	@Override
	public void info(String msg, Throwable t) {
		logger.log(Level.INFO, msg, t);
	}
	@Override
	public void info(Supplier<String> msgSupplier) {
		logger.info(msgSupplier);
	}

	@Override
	public void warn(String msg) {
		logger.warning(msg);
	}
	@Override
	public boolean isWarnEnabled() {
		return isEnabled(Level.WARNING);
	}
	@Override
	public void warn(String format, Object arg) {
		log(Level.WARNING, format, arg);
	}
	@Override
	public void warn(String format, Object arg1, Object arg2) {
		log(Level.WARNING, format, arg1, arg2);
	}
	@Override
	public void warn(String format, Object... arguments) {
		log(Level.WARNING, format, arguments);
	}
	@Override
	public void warn(String msg, Throwable t) {
		logger.log(Level.WARNING, msg, t);
	}


	@Override
	public void error(String msg) {
		logger.severe(msg);
	}
	@Override
	public boolean isErrorEnabled() {
		return isEnabled(Level.SEVERE);
	}
	@Override
	public void error(String format, Object arg) {
		log(Level.SEVERE, format, arg);
	}
	@Override
	public void error(String format, Object arg1, Object arg2) {
		log(Level.SEVERE, format, arg1, arg2);
	}
	@Override
	public void error(String format, Object... arguments) {
		log(Level.SEVERE, format, arguments);
	}
	@Override
	public void error(String msg, Throwable t) {
		logger.log(Level.SEVERE, msg, t);
	}

	@Override
	public void debug(Supplier<String> msgSupplier) {
		logger.fine(msgSupplier);
	}
	@Override
	public void debug(String msg) {
		logger.fine(msg);
	}
	@Override
	public boolean isDebugEnabled() {
		return isEnabled(Level.FINE);
	}
	@Override
	public void debug(String format, Object arg) {
		log(Level.FINE, format, arg);
	}
	@Override
	public void debug(String format, Object arg1, Object arg2) {
		log(Level.FINE, format, arg1, arg2);
	}
	@Override
	public void debug(String format, Object... arguments) {
		log(Level.FINE, format, arguments);
	}
	@Override
	public void debug(String msg, Throwable t) {
		logger.log(Level.FINE, msg, t);
	}
}
