import traceback
def run(func, args):
	try:
		return func(*args)
	except:
		raise Exception(traceback.format_exc(1))