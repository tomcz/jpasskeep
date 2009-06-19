<div id="entries">
    $entries:{ entry |
    <div class="entry">
        <div class="description">
            <span class="label">Description:</span>
            <span class="field">$entry.description$</span>
        </div>
        <div class="category">
            <span class="label">Category:</span>
            <span class="field">$entry.categoryName$</span>
        </div>
        <div class="username">
            <span class="label">Username:</span>
            <span class="field">$entry.username$</span>
        </div>
        <div class="password">
            <span class="label">Password:</span>
            <span class="field">$entry.password$</span>
        </div>
        <div class="notes">
            <span class="label">Notes:</span><br />
        <span class="field">
            $entry.notesAsLines:{ line | $line$<br /> }$
        </span>
        </div>
    </div>
    }$
</div>