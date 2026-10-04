const paymentForm = document.getElementById("paymentForm");
const paymentResult = document.getElementById("paymentResult");
const displayName = document.getElementById("displayName");
const displayAmount = document.getElementById("displayAmount");

paymentForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    const customerName =
        document.getElementById("customerName").value.trim();

    const amount =
        document.getElementById("amount").value;

    if (customerName.length === 0 || Number(amount) <= 0) {
        return;
    }

    const formData = new URLSearchParams();
    formData.append("customerName", customerName);
    formData.append("amount", amount);

    try {
        const response = await fetch("http://localhost:8080/payment", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: formData.toString()
        });

        if (!response.ok) {
            throw new Error("Payment request failed.");
        }

        // textContent prevents user input from being interpreted as HTML.
        displayName.textContent = customerName;
        displayAmount.textContent = Number(amount).toFixed(2);

        paymentResult.hidden = false;

    } catch (error) {
        alert("Unable to process payment.");
    }
});