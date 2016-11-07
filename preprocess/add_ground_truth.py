import argparse
import os
import csv
from collections import deque

# Listing format: ID, amount, rating, date, term
# Loan format: ID, amount, rating, date, term

LINES_TO_SKIP = 0

def main():
	parser = argparse.ArgumentParser()
	parser.add_argument("listing", type=str, help="Path to listings csv file")
	parser.add_argument("label", type=str, help="Path to loans csv file")
	args = parser.parse_args()

	dir_path = os.path.dirname(os.path.realpath(__file__))
	listing_file = os.path.join(dir_path, args.listing)
	label_file = os.path.join(dir_path, args.label)

	if not os.path.exists(listing_file):
		print("{} {}".format("Could not find listings csv file directory: ", listing_file))
		exit(1)

	if not os.path.exists(label_file):
		print("{} {}".format("Could not find loan csv file directory: ", label_file))
		exit(1)

	# Read rows from csv files and return dictionaries
	listings, num_listings = parse_csv_file(listing_file)
	labels, num_labels = parse_csv_file(label_file)
	
	# Want to output listing info and default label
	listing_keys = list(listings.keys())
	output = []
		
	for label in labels:
		if label in listings:
			# Add listing ID
			new_row = [label]
			# Add listing info
			new_row.extend(listings[label])
			# Add default label
			new_row.extend(labels[label])
			output.append(new_row)
			del listings[label]

	print("Writing to output file")

	# Save processed data to a new file
	with open(listing_file + ".labeled.csv", 'w') as f:
		f.write('')
		f.write("\n")

		for row in output:
			for idx_col in range(0, len(row)):
				f.write(row[idx_col])

				if idx_col == len(row) - 1:
					f.write("\n")
				else:
					f.write(",")    

	print("Matching complete")

def parse_csv_file(csv_file):
	"""
	"""
	print("Loading " + csv_file)
	contents = {}
	
	# Count the number of lines
	line_number = 0
	with open(csv_file, 'r') as f:
		for line in f:
			line_number += 1

	# Read each line into memory
	line_count = 0
	row_number = 0
	with open(csv_file, 'r') as f:
		reader = csv.reader(f)

		for data_line in reader:
			if(line_count > LINES_TO_SKIP):
				row_number +=  1

				value = []
				key = ''
				for idx, data in enumerate(data_line):
					data = data.strip("\"'")

					# Strip the time from the date value
					if idx == 0:
						key = data
					else:
						value.append(data)

				contents[key] = value

			line_count += 1

			if(line_count % 10000 == 0):
				print("{} {}".format(round((float(line_count)/line_number)*100.,1), "% complete"))

	print("Done loading " + csv_file)

	return (contents, row_number)

if __name__ == "__main__": main()