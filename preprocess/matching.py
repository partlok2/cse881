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
	parser.add_argument("loan", type=str, help="Path to loans csv file")
	args = parser.parse_args()

	dir_path = os.path.dirname(os.path.realpath(__file__))
	listing_file = os.path.join(dir_path, args.listing)
	loan_file = os.path.join(dir_path, args.loan)

	if not os.path.exists(listing_file):
		print("{} {}".format("Could not find listings csv file directory: ", listing_file))
		exit(1)

	if not os.path.exists(loan_file):
		print("{} {}".format("Could not find loan csv file directory: ", loan_file))
		exit(1)

	# Read rows from csv files and return deques
	listings, num_listings = parse_csv_file(listing_file)
	loans, num_loans = parse_csv_file(loan_file)
	
	# Want to output listing ID and loan status
	listings = deque(listings)
	output = []
		
	for idx_listing in range(0, num_listings):
		listing = listings.popleft()
		found = False
		idx_loan = 0
		
		while not found and idx_loan < num_loans:
			match = True
			
			for i in range(1, 5):
				if listing[i] != loans[idx_loan][i]:
					match = False
					break

			if match:
				output.append((listing[0], loans[idx_loan][5]))
				loans.pop(idx_loan)
				num_loans -= 1
				found = True
			elif listing[4] < loans[idx_loan][4]:
				idx_loan = num_loans

			idx_loan += 1

		if idx_listing % 10000 == 0:
			print("{} {}".format(round((float(idx_listing)/num_listings)*100.,1), "% listings complete"))
			print("{}{}".format("Num matched: ",len(output)))

	print("Writing to output file")

	# Save processed data to a new file
	with open(listing_file + ".status.csv", 'w') as f:
		column_idx = 0
		header_list = ["listing_number", "status"] # Need to know order we are writing to file in, dicts are not ordered
		header_list_len = len(header_list)

		for header in header_list:
			f.write(header)
			column_idx += 1

			if column_idx == header_list_len:
				f.write("\n")
			else:
				f.write(",")

		for row in output:
			for idx_col in range(0, header_list_len):
				f.write(row[idx_col])

				if idx_col == 1:
					f.write("\n")
				else:
					f.write(",")    

	print("Matching complete")

def parse_csv_file(csv_file):
	"""
	"""
	print("Loading " + csv_file)
	contents = []
	
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

				entry = []
				for idx, data in enumerate(data_line):
					data = data.strip("\"'")

					# Strip the time from the date value
					if idx == 4:
						data = data[0:10]
					
					entry.append(data)

				contents.append(tuple(entry))

			line_count += 1

			if(line_count % 10000 == 0):
				print("{} {}".format(round((float(line_count)/line_number)*100.,1), "% complete"))

	print("Done loading " + csv_file)

	return (contents, row_number)

if __name__ == "__main__": main()