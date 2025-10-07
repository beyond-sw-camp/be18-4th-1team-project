import { defineStore } from "pinia";
import { fetchMe } from "@/api/auth";

export const useUserStore = defineStore("user", {
  state: () => ({
    name: null,
    loginId: null,
    role:null,
    loaded: false, 
  }),
  actions: {
    async loadUser() {
      try {
        const res = await fetchMe();
        this.name = res.data?.userName ?? null;
        this.loginId = res.data?.loginId ?? null;
        this.role = res.data?.role ?? null;
      } catch {
        this.name = null;
        this.loginId = null;
        this.role = null;
      } finally {
        this.loaded = true;
      }
    },
    clear() {
      this.name = null;
      this.loginId = null;
      this.role = null;
      this.loaded = false;
    },
  },
});