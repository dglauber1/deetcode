function add_one(n) {
	return n + 1;
}
print(add_one.apply(null, [5]));