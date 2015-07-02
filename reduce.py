#!/usr/bin/python
import sys

def reducer():

	codes = []
	oldKey = None

	for line in sys.stdin:
		data = line.strip().split('\t')

		#sanity check
		if len(data) is not 2:
			continue

		thisKey, thisValue = data

		#print >> sys.stderr, oldKey is thisKey

		if oldKey and oldKey != thisKey:
			#print >> sys.stderr, oldKey, thisKey
			print "{0}\t{1}".format(oldKey, codes)
			codes = []
		
		oldKey = thisKey
		codes.append(thisValue)

	if oldKey is not None:
		print "{0}\t{1}".format(oldKey, codes)
		#print >> sys.stderr, 'LAST'
reducer()
