/* V2.9: Match Creation Logic (Frontend) */
const API_MATCHES_URL = 'http://localhost:8080/api/matches';
const setsContainer = document.getElementById('setsContainer');
const matchMessage = document.getElementById('matchMessage');
const createMatchForm = document.getElementById('createMatchForm');
const addSetButton = document.getElementById('addSetButton');
let setIndex = 0; // Tracks the number of sets added

// --- INITIALIZATION ---
document.addEventListener('DOMContentLoaded', function() {
    const token = localStorage.getItem('jwtToken');
    if (!token) {
        window.location.href = 'index.html'; // Redirect if not logged in
        return;
    }

    // Set today's date as default
    document.getElementById('matchDate').valueAsDate = new Date();

    // Setup Navigation
    setupNavigation();

    // Add initial three sets required for a match (Min 3)
    addSet();
    addSet();
    addSet();

    // Event listeners
    // REMOVED: addSetButton.addEventListener('click', addSet);
    // The handler is now in the HTML (onclick="addSet()")
    createMatchForm.addEventListener('submit', handleMatchSubmit);
});

function setupNavigation() {
    // Setup Logout Button
    document.getElementById('logoutButton').addEventListener('click', () => {
        localStorage.removeItem('jwtToken');
        window.location.href = 'index.html';
    });
}

// --- SET MANAGEMENT ---

/**
 * Adds a new input row for a match set (up to a max of 5).
 */
function addSet() {
    const currentSets = setsContainer.children.length;
    // Enforce Max Sets limit
    if (currentSets >= 5) {
        matchMessage.textContent = 'A match can have a maximum of 5 sets.';
        matchMessage.className = 'error-message';
        return;
    }

    // Use currentSets + 1 for the displayed set number
    const newSetNumber = currentSets + 1;
    setIndex = newSetNumber; // Keep setIndex updated with the current max set number
    matchMessage.textContent = ''; // Clear previous message

    const newSetRow = document.createElement('div');
    newSetRow.className = 'set-input-row';
    newSetRow.setAttribute('data-set-id', newSetNumber);

    newSetRow.innerHTML = `
        <span>Set ${newSetNumber}</span>
        <input type="number" data-score-type="homeScore" required min="0" value="0" placeholder="Our Score" aria-label="Our Score Set ${newSetNumber}">
        <input type="number" data-score-type="opponentScore" required min="0" value="0" placeholder="Opponent Score" aria-label="Opponent Score Set ${newSetNumber}">
        <button type="button" class="remove-set-btn" onclick="removeSet(this)">
            Remove
        </button>
    `;

    setsContainer.appendChild(newSetRow);

    // Update button states
    updateRemoveButtons();
}

/**
 * Removes a set row and re-indexes the remaining sets.
 */
window.removeSet = function(button) {
    // Enforce Min Sets limit
    if (setsContainer.children.length <= 3) {
        matchMessage.textContent = 'A match must have at least 3 sets.';
        matchMessage.className = 'error-message';
        return;
    }

    const rowToRemove = button.closest('.set-input-row');
    rowToRemove.remove();

    // Re-index all remaining set rows
    Array.from(setsContainer.children).forEach((row, index) => {
        const currentSetNumber = index + 1;
        row.querySelector('span').textContent = `Set ${currentSetNumber}`;
        row.setAttribute('data-set-id', currentSetNumber);

        // Update input aria labels
        row.querySelectorAll('input').forEach(input => {
            const scoreType = input.dataset.scoreType;
            input.setAttribute('aria-label', `${scoreType.replace('Score', ' Score')} Set ${currentSetNumber}`);
        });
    });

    // Update button states
    updateRemoveButtons();
}

/**
 * Hides remove buttons if only 3 sets are present, shows them otherwise,
 * and manages the Add Set button state.
 */
function updateRemoveButtons() {
    const rows = setsContainer.children;
    const isMinSets = rows.length <= 3;

    Array.from(rows).forEach(row => {
        const removeButton = row.querySelector('.remove-set-btn');
        if (removeButton) {
            removeButton.style.display = isMinSets ? 'none' : 'block';
        }
    });

    // Also disable 'Add Set' button if maxed out
    addSetButton.disabled = rows.length >= 5;
    addSetButton.textContent = rows.length >= 5 ? 'Max Sets Reached' : 'Add Set';
}

// --- FORM SUBMISSION ---

/**
 * Collects match data and sends it as a POST request to the backend.
 */
async function handleMatchSubmit(event) {
    event.preventDefault();

    matchMessage.textContent = 'Submitting match data...';
    matchMessage.className = 'success-message'; // Temporarily green for loading

    const token = localStorage.getItem('jwtToken');
    if (!token) {
        matchMessage.textContent = 'Authentication failed. Please log in.';
        matchMessage.className = 'error-message';
        return;
    }

    // 1. Collect Set Data
    const sets = [];
    const setRows = setsContainer.querySelectorAll('.set-input-row');

    setRows.forEach((row, index) => {
        const homeScoreInput = row.querySelector('input[data-score-type="homeScore"]');
        const opponentScoreInput = row.querySelector('input[data-score-type="opponentScore"]');

        sets.push({
            setNumber: index + 1,
            homeScore: parseInt(homeScoreInput.value, 10),
            opponentScore: parseInt(opponentScoreInput.value, 10)
        });
    });

    // 2. Construct the final MatchRequestDTO payload
    const matchPayload = {
        opponentTeam: document.getElementById('opponentTeam').value,
        matchDate: document.getElementById('matchDate').value,
        sets: sets,
        homeSetScore: 0,
        opponentSetScore: 0,
    };

    // 3. Send the request
    try {
        const response = await fetch(API_MATCHES_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(matchPayload)
        });

        if (response.ok) {
            matchMessage.textContent = 'Match created successfully! Redirecting to dashboard...';
            matchMessage.className = 'success-message';

            // Wait a moment then redirect
            setTimeout(() => {
                window.location.href = 'dashboard.html';
            }, 1500);

        } else {
            const errorText = await response.text();
            matchMessage.textContent = `Error creating match: ${errorText || response.statusText}`;
            matchMessage.className = 'error-message';
        }

    } catch (error) {
        console.error('Network error during match creation:', error);
        matchMessage.textContent = 'Network error. Could not connect to the server.';
        matchMessage.className = 'error-message';
    }
}