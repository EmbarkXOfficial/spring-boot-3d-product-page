# Cartwell product page: starter

This is the starter project for the video. It's a small Spring Boot store with one product page
for a handbag. Right now the page shows a normal photo of the bag.

In the video we replace that photo with a 3D model of the bag. You'll be able to rotate it, zoom
in, and on a phone, place it on your table with AR. We make the model from the same photo with an
image-to-3D tool.

Code along with me. Each step below matches a part of the video, and after each one there's a
quick check so you know you're on track.

## What you need

- Java 25 or newer
- An IDE (I use IntelliJ, but any is fine)
- A tool that turns a photo into a 3D model and exports GLB. I use
  [Tripo AI](https://studio.tripo3d.ai/home?invite_code=GKXT3W&utm_source=YouTube&utm_medium=kol&utm_campaign=kol_EmbarkX),
  but any tool that gives you a GLB file works
- Optional, for AR: an Android phone with a USB cable, or an iPhone plus [ngrok](https://ngrok.com)

If you want to follow along, sign up to
[Tripo Studio](https://studio.tripo3d.ai/home?invite_code=GKXT3W&utm_source=YouTube&utm_medium=kol&utm_campaign=kol_EmbarkX)
with my invite code and get 500 bonus credits. And code `GKXT3W` gets you 60% off your first month
of Pro.

You don't need to install Maven. The project comes with the Maven wrapper (`mvnw`).

## The project

```
src/main/java/com/embarkx/shop
├── ShopApplication.java
└── product
    ├── Product.java            the product (a record)
    ├── ProductService.java     the catalogue, kept in memory
    └── ProductController.java  GET / and GET /products/{id}

src/main/resources
├── static/images/handbag.jpg   the product photo
├── static/css/store.css        page styles
└── templates/                  product page, header and footer, 404 page
```

There's no database and no login. That's on purpose, so we can focus on the 3D part.

The styles for the 3D viewer are already in `store.css`, so you won't have to write any CSS.

---

## Step 0: Run the starter

```bash
./mvnw spring-boot:run
```

Open http://localhost:8080. It redirects to `/products/1`.

**Check:** you see the handbag page with the photo on the left.

Keep the app running in a terminal. After each step, stop it (Ctrl+C) and run it again.

---

## Step 1: Make the 3D model

I'm using Tripo here. If you use another tool, the export settings are the part that matters.

If you want to follow along, sign up to
[Tripo Studio](https://studio.tripo3d.ai/home?invite_code=GKXT3W&utm_source=YouTube&utm_medium=kol&utm_campaign=kol_EmbarkX)
with my invite code and get 500 bonus credits. And code `GKXT3W` gets you 60% off your first month
of Pro.

1. Open Tripo and pick **Image to 3D**.
2. Upload the photo from this project: `src/main/resources/static/images/handbag.jpg`.
3. Wait for the model, then click **Export** and use these settings:
   - Format: **GLB**. It's a single file with the textures packed inside.
   - Textures: **2K** (1K works too).
   - Face count: around **50k to 100k**. This is what decides the file size.
   - Aim for a file **under 10 MB** so it loads quickly on a phone.
4. Create a `models` folder in `src/main/resources/static/` and save the file there as
   `fashion-handbag.glb`.

If you skip the face count, the file comes out at around 55 MB. It still works, it just takes a
long time to load.

**Check:** restart the app and open http://localhost:8080/models/fashion-handbag.glb. The browser
should download the file.

We didn't write any code for this. Spring Boot serves everything inside `static/` automatically.

---

## Step 2: Point the product at the model

The product currently stores the URL of its photo. We'll change it to store the URL of its model.

In `src/main/java/com/embarkx/shop/product/Product.java`, change the comment:

```java
 * @param imageUrl product photo shown on the page (files in static/images/)
```

to:

```java
 * @param modelUrl where the browser loads the 3D model from (files in static/models/)
```

And rename the last field from `imageUrl` to `modelUrl`:

```java
		String modelUrl) {
```

In `src/main/java/com/embarkx/shop/product/ProductService.java`, update the class comment:

```java
 * In-memory product catalogue. To add a product, put its GLB file in static/models/ and add an
```

And change the photo path to the model path:

```java
					"/models/fashion-handbag.glb"));
```

That's the only backend change. The controller already passes `product` to the page.

Don't worry if the page throws an error now. The template still asks for `imageUrl`, and we fix
that in the next step.

---

## Step 3: Show the model on the page

We're using [`<model-viewer>`](https://modelviewer.dev), a web component from Google. You add it
with a script tag and use it like any other HTML element.

### 3.1 Add the scripts

Open `src/main/resources/templates/product.html`. In `<head>`, right after the `store.css` line,
add:

```html
  <!-- Google's <model-viewer> web component, pinned to an exact version -->
  <script type="module" src="https://ajax.googleapis.com/ajax/libs/model-viewer/4.3.1/model-viewer.min.js"></script>
  <script th:src="@{/js/product.js}" defer></script>
```

The second script is `product.js`. It doesn't exist yet. We write it in step 4.

### 3.2 Replace the photo

In the same file, find this block:

```html
    <!-- Left column: product photo -->
    <section class="product__media" aria-label="Product images">
      <div class="viewer-frame">
        <img th:src="${product.imageUrl}" th:alt="${product.name}"
             style="width: 100%; height: 100%; object-fit: contain;">
      </div>
```

and replace it with:

```html
    <!-- Left column: interactive 3D view -->
    <section class="product__media" aria-label="Product images">
      <div class="viewer-frame">
        <model-viewer id="product-viewer"
                      class="viewer"
                      th:src="${product.modelUrl}"
                      th:alt="|3D model of the ${product.name}|"
                      camera-controls
                      touch-action="pan-y"
                      auto-rotate
                      auto-rotate-delay="1500"
                      rotation-per-second="18deg"
                      interaction-prompt="none"
                      camera-orbit="35deg 75deg auto"
                      shadow-intensity="1"
                      shadow-softness="0.8"
                      environment-image="neutral"
                      exposure="1.05">
        </model-viewer>
      </div>
```

A quick tour of the attributes:

- `th:src` is Thymeleaf filling in `/models/fashion-handbag.glb`.
- `camera-controls` lets you drag to rotate and scroll or pinch to zoom.
- `touch-action="pan-y"` keeps page scrolling working on phones.
- `auto-rotate`, `auto-rotate-delay` and `rotation-per-second` give it a slow spin after 1.5
  seconds. It stops once you touch it.
- `interaction-prompt="none"` hides the built-in hand animation. We add our own hint in step 4.
- `camera-orbit` sets the starting angle, slightly to the side and from a bit above.
- `shadow-intensity` and `shadow-softness` put a soft shadow under the bag.
- `environment-image="neutral"` and `exposure` control the lighting.

**Check:** restart and reload. The bag shows up in 3D and slowly turns. Drag it around and zoom in.

---

## Step 4: Loading bar, hint and fallback

The model takes a second or two to download. So we'll show a loading bar, a small "drag to rotate"
hint, and a message if the model can't load.

### 4.1 The HTML

Paste this inside `<model-viewer>`, between the opening tag and `</model-viewer>`:

```html

          <div class="viewer__loading" slot="progress-bar" id="viewer-loading">
            <span class="spinner" aria-hidden="true"></span>
            <span class="viewer__loading-text">Loading 3D model… <span id="viewer-progress">0%</span></span>
            <span class="viewer__bar"><span class="viewer__bar-fill" id="viewer-bar"></span></span>
          </div>

          <div class="viewer__hint" id="viewer-hint">
            <svg viewBox="0 0 24 24" width="16" height="16" aria-hidden="true">
              <path d="M12 3a9 9 0 1 0 9 9" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              <path d="M21 4v5h-5" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            View in 3D, drag to rotate
          </div>
```

`slot="progress-bar"` tells model-viewer to use our loading bar instead of its own.

Then, right after `</model-viewer>` (still inside the `viewer-frame` div), add the fallback. It
starts hidden:

```html

        <div class="viewer-fallback" id="viewer-fallback" role="status" hidden>
          <svg viewBox="0 0 64 64" width="64" height="64" aria-hidden="true">
            <path d="M6 42c8 0 14-3 20-10l6-8 6 5c4 3 10 5 16 6 4 1 6 3 6 6v3H6v-2z" fill="none"
                  stroke="currentColor" stroke-width="2.5" stroke-linejoin="round"/>
            <path d="M6 44h54" stroke="currentColor" stroke-width="2.5"/>
          </svg>
          <p class="viewer-fallback__title">3D preview unavailable</p>
          <p class="viewer-fallback__text">We couldn't load the interactive 3D model right now. Please check back in a little while.</p>
        </div>
```

### 4.2 The JavaScript

Create a new file, `src/main/resources/static/js/product.js`:

```js
// Product page behaviour for <model-viewer>: loading progress, the drag hint and the
// fallback message. The page works without this file; it only
// adds interactivity on top of the server-rendered HTML.
(() => {
  const viewer = document.getElementById('product-viewer');
  if (!viewer) {
    return;
  }

  const fallback = document.getElementById('viewer-fallback');
  const loading = document.getElementById('viewer-loading');
  const progressText = document.getElementById('viewer-progress');
  const progressBar = document.getElementById('viewer-bar');
  const hint = document.getElementById('viewer-hint');

  // Loading indicator
  viewer.addEventListener('progress', (event) => {
    const percent = Math.round(event.detail.totalProgress * 100);
    progressText.textContent = `${percent}%`;
    progressBar.style.width = `${percent}%`;
    loading.classList.toggle('is-done', percent >= 100);
  });

  // The model is missing, failed to load, or WebGL is unavailable: swap in the fallback message.
  viewer.addEventListener('error', () => {
    viewer.hidden = true;
    fallback.hidden = false;
  });

  // Fade the "drag to rotate" hint once the shopper has interacted.
  viewer.addEventListener('camera-change', (event) => {
    if (event.detail.source === 'user-interaction') {
      hint.classList.add('is-faded');
    }
  });
})();
```

We listen to three events from model-viewer:

- `progress` fires while the file downloads. We update the percentage and the bar.
- `error` fires if the file is missing or broken, or the browser can't do 3D. We hide the viewer
  and show the fallback.
- `camera-change` fires when the view moves. Once it's the shopper moving it, we fade the hint.

**Check:** restart and do a hard reload (Shift + Reload).

- You see "Loading 3D model…" count up to 100%.
- The hint fades after you drag the bag.
- Rename the GLB to `fashion-handbag.bak`, restart and reload. You should see "3D preview
  unavailable". Rename it back when you're done.

---

## Step 5: View it in your room (AR)

### 5.1 Turn on AR

Add two attributes to the end of the `<model-viewer>` opening tag. Change:

```html
                      exposure="1.05">
```

to:

```html
                      exposure="1.05"
                      ar
                      ar-modes="webxr scene-viewer quick-look">
```

`ar` turns AR on. `ar-modes` is the order to try things in: WebXR in Chrome on Android, then
Google's Scene Viewer, then Quick Look on iPhone. For iPhone, model-viewer converts the GLB for you.

### 5.2 Add the AR button

Inside `<model-viewer>`, after the hint div and before `</model-viewer>`, add:

```html

          <!-- model-viewer only shows this slot on devices that support AR -->
          <button class="ar-button" slot="ar-button" type="button">
            <svg viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
              <path d="M12 2l8 4.5v9L12 20l-8-4.5v-9L12 2z M4 6.5l8 4.5 8-4.5 M12 11v9" fill="none"
                    stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/>
            </svg>
            View in your room
          </button>
```

You won't see this button on your laptop. model-viewer only shows it on devices that support AR.

### 5.3 Open it on your phone

AR only works on `https://` pages or on `localhost`, and your phone can't see your laptop's
`localhost`. Two ways around that:

- Android with a USB cable: run `adb reverse tcp:8080 tcp:8080`, then open
  `http://localhost:8080` in Chrome on the phone.
- Any phone: run `ngrok http 8080` and open the `https://` link it gives you.

**Check:** tap **View in your room** and put the bag on your table.

If the bag looks huge, that's the size the model was exported at. Generated models often come
out about a metre tall. Scale it to real size in your 3D tool before exporting.

And that's it. You've got a 3D product page with AR.

---

## Want to add another product?

No new code needed:

1. Make a GLB from the product photo (step 1) and put it in `static/models/`.
2. Add an entry to the `products` map in `ProductService` and set its `modelUrl`.

## If something's not working

- **Error page after step 2:** you haven't done step 3.2 yet. The template still uses `imageUrl`.
- **"3D preview unavailable":** the file name or folder is wrong. Open `/models/<your-file>.glb`
  in the browser. It should download.
- **Stuck at "Loading 0%":** the browser can't find `product.js`. Make sure it's in `static/js/`,
  then hard reload.
- **Takes forever to load:** the file is too big. Export again with fewer faces.
- **No AR button:** you're on a laptop, on plain `http://` on a phone, or the phone doesn't support
  AR. See step 5.3.

---

Cartwell and Maison Vale are made-up names for this demo.
