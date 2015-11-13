#!/usr/bin/perl
#######################################################################################################################
##
## @author mgreenwood
## Converts a plugin.xml configuration from the XML file defining the columns within the tables we want access to
## into a named query file that provides a list of queries given the name prefix of com.edpanel.queries.$table where
## table is the name of the powerschool table.
##
## A complete list of tables is provided via the text CSV document:
## 	schema.csv
## This file includes the table name, the field name and the column type
##
#######################################################################################################################

my %tables;
open(FILE, "< plugin.xml");
for my $line (<FILE>) {
	$line =~ s/\n//g;
	if ($line =~ /<field table=\"(\w+)\" field=\"(\w+)\"/) {
		my $table=$1;
		my $field=$2;
		push @{$tables{$table}}, $field;
	}
}
close FILE;

my $xmlQueryConfig = "<queries>\n";

for my $table (sort keys %tables) {
	my $columns = "\t\t<columns>\n";
	my $sql = "\t\t<sql>\n\t\t\t<![CDATA[\n\t\t\tSELECT\n";
	$xmlQueryConfig .= "\t<query name=\"com.edpanel.queries.$table\" coreTable=\"\" flattened=\"false\">\n\t\t<args></args>\n";
	
	my $count = 0;
	my $hasIDField=0;
	for my $field (@{$tables{$table}}) {
		if ($field eq "ID") {
			$hasIDField = 1;
		}
		$columns .= "\t\t\t<column column=\"$table.$field\">$field</column>\n";
		if ($count > 0) {
			$sql .= ",\n";
		}
		$sql .= "\t\t\t\t$field";
		$count++;
	}
	$columns .= "\t\t</columns>\n";
	$sql .= "\n\t\t\tFROM\n\t\t\t\t$table";
	if ($hasIDField) {
		$sql .= "\n\t\t\tORDER BY ID";
	}
	$sql .= ";\n\t\t\t]]>\n\t\t</sql>\n";
	$xmlQueryConfig .= $columns . $sql . "\n\t</query>\n";
}

$xmlQueryConfig .= "</queries>\n";
print $xmlQueryConfig;
