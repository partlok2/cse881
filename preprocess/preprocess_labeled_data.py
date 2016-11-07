#!/usr/bin/env python
import argparse
import os
import csv
import statistics

'''
Remove rows with columns that are empty and necessary (state, homeowner)
Format values (fico score)
Fill in missing values
Replace bad values
'''

STANDARDIZE = "standardize"
CATEGORIZE = "categorize"
BINARIZE = "binarize"

#######################################################
################ Configuration ########################
#######################################################
# Any meta-data we want to skip over; 
# how many rows until we get the header
LINES_TO_SKIP = 0

# column_name => STANDARDIZE|CATEGORIZE|BINARIZE|NOTHING

required_features = [
    "borrower_state",
    "is_homeowner",
    "prosper_rating"    
]

data_features = {
    "loan_origination_date": STANDARDIZE,
    "listing_amount": STANDARDIZE,
    "estimated_return": STANDARDIZE,
    "estimated_loss_rate": STANDARDIZE,
    "lender_yield": STANDARDIZE,
    "listing_term": BINARIZE,
    "listing_monthly_payment": STANDARDIZE,
    "fico_score": STANDARDIZE,
    "prosper_score": CATEGORIZE,
    "stated_monthly_income": STANDARDIZE,
    "dti_wprosper_loan": STANDARDIZE,
    "occupation": CATEGORIZE,
    "months_employed": STANDARDIZE,
    "borrower_state": CATEGORIZE,
    "prior_prosper_loans_active": STANDARDIZE,
    "prior_prosper_loans": STANDARDIZE,
    "monthly_debt": STANDARDIZE,
    "current_delinquencies": STANDARDIZE,
    "delinquencies_last7_years": STANDARDIZE,
    "first_recorded_credit_line": STANDARDIZE,
    "credit_lines_last7_years": STANDARDIZE,
    "inquiries_last6_months": STANDARDIZE,
    "amount_delinquent": STANDARDIZE,
    "current_credit_lines": STANDARDIZE,
    "bankcard_utilization": STANDARDIZE,
    "total_open_revolving_accounts": STANDARDIZE,
    "installment_balance": STANDARDIZE,
    "real_estate_balance": STANDARDIZE,
    "revolving_balance": STANDARDIZE,
    "real_estate_payment": STANDARDIZE,
    "total_inquiries": STANDARDIZE,
    "total_trade_items": STANDARDIZE,
    "satisfactory_accounts": STANDARDIZE,
    "delinquencies_over30_days": STANDARDIZE,
    "delinquencies_over60_days": STANDARDIZE,
    "delinquencies_over90_days": STANDARDIZE,
    "is_homeowner": BINARIZE
}

class_labels = [
    "listing_number",
    "label",
    "prosper_rating"
]

#######################################################
#######################################################
#######################################################

def main():
    """
    """
    parser = argparse.ArgumentParser()
    parser.add_argument("csv_file", type=str, help="Path to CSV file")
    args = parser.parse_args()
    
    if(not os.path.exists(args.csv_file)):
        print("Could not find CSV file directory")
        exit(1)
        
    # Read column data from CSV file
    column_table, num_rows = parse_csv_file(args.csv_file)

    # Process the columns we want
    output_columns = {}

    for column_name in column_table:
        if column_name in data_features:
            method = data_features[column_name]

            if method == STANDARDIZE:
                try:
                    output_columns[column_name] = standardize_column(column_table[column_name])
                except Exception as e:
                    print("Error on column", column_name, e)
            
            elif method == CATEGORIZE:
                for new_name, new_column in categorize_column(column_table[column_name], column_name).items():
                    output_columns[new_name] = new_column
            
            elif method == BINARIZE:
                if column_name == "is_homeowner":
                    output_columns[column_name] = binarize_column(column_table[column_name], "False", "True")
                
                elif column_name == "listing_term":
                    output_columns[column_name] = binarize_column(column_table[column_name], "36", "60")
        
        elif column_name in class_labels:
            output_columns[column_name] = column_table[column_name]
        
        else:
            print("Skipping column " + column_name)
    
    ###### Need to output to separate files ######
    # Ending row number for each prosper rating 
    rating_row_range = { "A": 20362, "AA": 29599, "B": 50494, "C": 75551, "D": 95953, "E": 108738, "HR": 122414 }
    ratings = sorted(rating_row_range.keys())
    row_number = 0

    # Save processed data to a new file
    with open(args.csv_file + ".all.csv", 'w') as f:
        column_idx = 0
        header_list = [] # Need to know order we are writing to file in, dicts are not ordered
        num_columns = len(output_columns)

        for column_name in sorted(output_columns.keys()):
            header_list.append(column_name)
            f.write(column_name)
            column_idx += 1

            if(column_idx == num_columns):
                f.write("\n")
            else:
                f.write(",")
                
        for row in range(num_rows):
            column_idx = 0

            for header in header_list:
                f.write(str(output_columns[header][row]))
                column_idx += 1

                if(column_idx == num_columns):
                    f.write("\n")
                else:
                    f.write(",")   

    print("Finished all ratings output file")

    start_row = 0
    for rating in ratings:
        print_rating_file(output_columns, rating, start_row, rating_row_range[rating])
        start_row = rating_row_range[rating]
        print("{}{}{}".format("Finished ", rating, " ratings output file"))

def print_rating_file(output_columns, file_suffix, start_row, end_row):
   
    with open("labeled." + file_suffix + ".csv", 'w') as f:
        column_idx = 0
        header_list = [] # Need to know order we are writing to file in, dicts are not ordered
        num_columns = len(output_columns)

        for column_name in sorted(output_columns.keys()):
            header_list.append(column_name)
            f.write(column_name)
            column_idx += 1

            if(column_idx == num_columns):
                f.write("\n")
            else:
                f.write(",")
                
        for row in range(start_row, end_row):
            column_idx = 0

            for header in header_list:
                f.write(str(output_columns[header][row]))
                column_idx += 1

                if(column_idx == num_columns):
                    f.write("\n")
                else:
                    f.write(",") 

def standardize_column(column):
    """ Standardize column data
    """
    # Convert to floats
    for idx,value in enumerate(column):
        try:
            column[idx] = float(value.strip("% "))
        except:
            break
            
    # Find mean and std dev
    mean = statistics.mean(column)
    stddev = statistics.stdev(column)

    for idx,value in enumerate(column):
        column[idx] = round((value - mean)/stddev,4)
    
    return column

def categorize_column(column, column_name):
    """ Create new binary asymmetric columns from a single column with discrete values.
    """
    # Get set of unique values
    discrete_values = set()
    for value in column:
        discrete_values.add(value)
        
    # Create a column for each value
    new_columns = {}
    for discrete_value in discrete_values:
        new_columns[column_name + "_" + discrete_value] = []
        
    # Assign 0/1 based on value and column
    for discrete_value in discrete_values:
        for value in column:
            if value == discrete_value:
                new_columns[column_name + "_" + discrete_value].append(1)
            else:
                new_columns[column_name + "_" + discrete_value].append(0)

    return new_columns

def binarize_column(column, min_val, max_val):
    """ 
    Change a column with two discrete values to binary 0/1
    Missing values become 0
    """
        
    for idx,value in enumerate(column):
        if value == max_val:
            column[idx] = 1
        else:
            column[idx] = 0

    return column                           

def parse_csv_file(csv_file):
    """
    """
    print("Loading " + csv_file)
    header_index_table = {}
    column_table = {}
    
    # Count the number of lines
    line_number = 0
    with open(csv_file, 'r') as f:
        for line in f:
            line_number += 1
    
    # Read each line into memory
    line_count = 0
    num_rows = 0
    with open(csv_file, 'r') as f:
        reader = csv.reader(f)
        
        for data_line in reader:
            if(line_count > LINES_TO_SKIP):
                
                if(not check_line(data_line, header_index_table)): continue
                
                num_rows +=  1
                
                for idx, data in enumerate(data_line):
                    data = data.strip("\"'")
                    column_table[header_index_table[idx]].append(data)
            
            elif(line_count == LINES_TO_SKIP):
                for idx, header in enumerate(data_line):
                    header = header.strip("\"'")
                    header_index_table[idx] = header
                    column_table[header] = []
                    
            line_count += 1
            
            if line_count % 10000 == 0:
                print(round(float(line_count/line_number)*100,2), "% complete")
                
    print("Done loading " + csv_file)
    
    return (column_table, num_rows)

def check_line(data_line, header_index_table):
    """ 
    Skip a row of data if required features are missing
    """
    if len(data_line) < 2: return False
    
    for idx, data in enumerate(data_line):  
        data = data.strip("\"'")  
        
        if (data == "") and (header_index_table[idx] in required_features): return False
   
    return True
    
if __name__ == "__main__": main()