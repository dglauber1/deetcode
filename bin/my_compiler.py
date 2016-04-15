import traceback
import py_compile
def compile_mod(module):
	try:
		py_compile.compile(module, doraise=True)
		return 0
	except:
		return traceback.format_exc(1)