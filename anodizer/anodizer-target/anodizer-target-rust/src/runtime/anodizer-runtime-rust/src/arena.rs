use bumpalo::Bump;

pub struct Arena {
    bump: Bump,
}

impl Default for Arena {
    fn default() -> Self {
        Self::new()
    }
}

impl Arena {
    pub fn new() -> Self {
        Arena { bump: Bump::new() }
    }

    pub fn alloc<T>(&self, val: T) -> &mut T {
        self.bump.alloc(val)
    }
}
