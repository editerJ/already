# Already Legal Pages

This folder is ready for GitHub Pages publishing.

## Recommended structure

- `docs/index.html`
- `docs/privacy-policy.html`
- `docs/terms-of-service.html`

## GitHub Pages setup

1. Push this project to GitHub.
2. In the repository settings, enable GitHub Pages.
3. Set the source to `Deploy from a branch`.
4. Choose the main branch and the `/docs` folder.
5. After Pages is published, copy the base URL.

Example:

`https://<github-username>.github.io/<repo-name>`

## App configuration

Set these values in `local.properties` before building a release:

```properties
FUTURE_SELF_SUPPORT_EMAIL=your-support-email@example.com
FUTURE_SELF_GITHUB_PAGES_BASE_URL=https://<github-username>.github.io/<repo-name>
```

When `FUTURE_SELF_GITHUB_PAGES_BASE_URL` is present, the app can derive:

- `privacy-policy.html`
- `terms-of-service.html`

automatically for release configuration.