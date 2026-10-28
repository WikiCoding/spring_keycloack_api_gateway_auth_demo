# Products UI notes

- Keep HTTP calls in `src/api/client.js`; the UI calls only the API gateway.
- Use only `useState`, `useContext`, `useEffect`, `useRef`, or `useNavigation` hooks unless approval is obtained.
- Keep reusable UI controls in `src/components`.
