<b>Description:</b> <tt>$entry.description$</tt><br>
<b>Category:</b> <tt>$entry.categoryName$</tt><br>
$if(showPasswd)$
<b>Username:</b> <tt>$entry.username$</tt><br>
<b>Password:</b> <tt>$entry.password$</tt><br>
$endif$
$if(showNotes)$
<hr>
<b>Notes:</b>
$entry.notesAsLines:{ line | <br><tt>$line$</tt> }$
$endif$