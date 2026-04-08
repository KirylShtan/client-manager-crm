(() => {
  console.log("[autofill] content script loaded:", location.href);
  function setNativeValue(el, value) {
    const proto = Object.getPrototypeOf(el);
    const desc = Object.getOwnPropertyDescriptor(proto, "value");
    if (desc?.set) desc.set.call(el, value);
    else el.value = value;
    el.dispatchEvent(new Event("input", { bubbles: true }));
    el.dispatchEvent(new Event("change", { bubbles: true }));
  }
  function readPayload() {
    
    const hash = location.hash || "";
    const qs = new URLSearchParams(location.search);
    let encoded = null;
    if (hash.startsWith("#autofill=")) encoded = decodeURIComponent(hash.slice(10));
    else if (qs.has("autofill")) encoded = qs.get("autofill");
    if (!encoded) {
      console.warn("[autofill] no payload in url");
      return null;
    }
    try {
      const data = JSON.parse(atob(encoded));
      console.log("[autofill] payload parsed", data);
      return data;
    } catch (e) {
      console.error("[autofill] payload parse failed", e);
      return null;
    }
  }
  function findInputs() {
    const caseInput =
      document.querySelector("#nr_spawy") ||
      document.querySelector('input[name="nr_spawy"]') ||
      document.querySelector("#nr_sprawy") ||
      document.querySelector('input[name="nr_sprawy"]');
    const passInput =
      document.querySelector("#kod") ||
      document.querySelector('input[name="kod"]');
    return { caseInput, passInput };
  }
  function tryFill(data) {
    const { caseInput, passInput } = findInputs();
    if (!caseInput || !passInput) {
      console.warn("[autofill] inputs not found yet");
      return false;
    }
    setNativeValue(caseInput, data.caseNumber || "");
    setNativeValue(passInput, data.password || "");
    console.log("[autofill] filled OK");
    return true;
  }
  const data = readPayload();
  if (!data) return;
  let tries = 0;
  const t = setInterval(() => {
    tries++;
    if (tryFill(data) || tries > 40) clearInterval(t);
  }, 250);
})();