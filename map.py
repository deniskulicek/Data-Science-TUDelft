#!/usr/bin/python
import sys

def mapper():
	lineno = 0
	for line in sys.stdin:
		data = line.strip().split(",")
		lineno += 1;

		#sanity check
		if lineno < 2 or len(data) is not 5:
			continue

		id1, id2, geo_pt1, geo_pt2, median = data

		print "{0}\t{1}".format(median, id2)

mapper()
