import traceback
def run(func, args):
	try:
		return func(*args)
	except:
		return traceback.format_exc()