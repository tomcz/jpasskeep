import csv
import sys
import xml.etree.ElementTree as ET
from typing import NamedTuple


class Entry(NamedTuple):
    description: str
    category: str
    username: str
    password: str
    notes: str


def main(src_file, dest_file):
    entries = []
    with open(src_file) as fp:
        r = csv.DictReader(fp)
        fields = set(r.fieldnames) - {'Title', 'Tags', 'Username', 'Password', 'Created Date', 'Modified Date', 'Type'}
        for row in r:
            title = row['Title']
            if not title:
                continue
            tags = row['Tags']
            if tags:
                tags = tags.removeprefix('{(')
                tags = tags.removesuffix(')}')
                tags = tags.strip()
            if tags not in {'Home', 'Work', 'Other'}:
                tags = 'Home'
            notes = []
            for field in fields:
                value = row[field]
                if value:
                    value = value.strip()
                if value:
                    notes.append(value)
            entries.append(Entry(
                title,
                tags,
                row['Username'],
                row['Password'],
                '\n'.join(notes),
            ))

    root = ET.Element('list')
    for entry in entries:
        elt = ET.SubElement(root, 'entry')
        ET.SubElement(elt, 'description').text = entry.description
        ET.SubElement(elt, 'category').text = entry.category
        ET.SubElement(elt, 'username').text = entry.username
        ET.SubElement(elt, 'password').text = entry.password
        ET.SubElement(elt, 'notes').text = entry.notes

    tree = ET.ElementTree(root)
    tree.write(dest_file, 'utf-8', True)


if __name__ == '__main__':
    main(sys.argv[1], sys.argv[2])
