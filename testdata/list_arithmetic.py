def add_lists(l1, l2):
	to_return = []
	for i in range(len(l1)):
		to_return.append(l1[i] + l2[i])
	return to_return

def subtract_lists(l1, l2):
	to_return = []
	for i in range(len(l1)):
		to_return.append(l1[i] - l2[i])
	return to_return

def divide_lists(l1, l2):
	to_return = []
	for i in range(len(l1)):
		to_return.append(l1[i] / l2[i])
	return to_return